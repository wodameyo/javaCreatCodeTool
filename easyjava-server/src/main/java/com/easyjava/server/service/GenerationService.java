package com.easyjava.server.service;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.engine.CodeGenerator;
import com.easyjava.core.engine.GeneratedFile;
import com.easyjava.core.engine.GenerationPlan;
import com.easyjava.core.engine.GenerationSummary;
import com.easyjava.core.engine.OutputWriter;
import com.easyjava.core.engine.ZipExporter;
import com.easyjava.core.metadata.MetadataReader;
import com.easyjava.core.model.TableModel;
import com.easyjava.server.dto.FilePreview;
import com.easyjava.server.dto.GenerateRequest;
import com.easyjava.server.dto.GenerateResponse;
import com.easyjava.server.dto.GenerationSummaryDto;
import com.easyjava.server.dto.GeneratorConfigRequest;
import com.easyjava.server.dto.PreviewRequest;
import com.easyjava.server.dto.PreviewResponse;
import com.easyjava.server.web.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 控制台的预览与生成编排，所有生成逻辑都委托给 core 的 CodeGenerator。
 */
@Service
public class GenerationService {

    private static final Logger logger = LoggerFactory.getLogger(GenerationService.class);

    private static final DateTimeFormatter ZIP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private static final long MAX_PREVIEW_BYTES = 1_000_000L;

    private final DataSourceSessionService sessionService;

    private final GeneratorConfigFactory configFactory;

    private final DownloadTokenService tokenService;

    private final PathGuard pathGuard;

    public GenerationService(DataSourceSessionService sessionService,
                             GeneratorConfigFactory configFactory,
                             DownloadTokenService tokenService,
                             PathGuard pathGuard) {
        this.sessionService = sessionService;
        this.configFactory = configFactory;
        this.tokenService = tokenService;
        this.pathGuard = pathGuard;
    }

    public PreviewResponse preview(PreviewRequest request) {
        Prepared prepared = prepare(request.sessionId(), request.tables(), request.config());
        GenerationPlan plan = new CodeGenerator(prepared.config()).generate(prepared.tables());

        List<FilePreview> files = new ArrayList<>();
        int existingCount = 0;
        for (GeneratedFile file : plan.getFiles()) {
            Path target = file.getTargetPath();
            pathGuard.ensureAllowed(target, "目标文件");
            boolean exists = Files.isRegularFile(target);
            String existingContent = null;
            if (exists) {
                existingCount++;
                existingContent = readExisting(target);
            }
            boolean changed = !exists || !Objects.equals(file.getContent(), existingContent);
            files.add(new FilePreview(file.getLogicalPath(), file.getKind().name(),
                    target.toString(), exists, changed, file.getContent(), existingContent));
        }
        return new PreviewResponse(files, files.size(), existingCount,
                prepared.config().getOutput().getOverwritePolicy().name());
    }

    public GenerateResponse generate(GenerateRequest request) {
        long start = System.currentTimeMillis();
        Prepared prepared = prepare(request.sessionId(), request.tables(), request.config());
        GenerationPlan plan = new CodeGenerator(prepared.config()).generate(prepared.tables());

        boolean writeToDisk = Boolean.TRUE.equals(request.writeToDisk());
        GenerationSummary summary = new OutputWriter().write(plan, !writeToDisk);
        logger.info("控制台生成完成: files={}, writeToDisk={}, {}", plan.getFileCount(),
                writeToDisk, summary.describe());

        ZipFile zip = exportZip(plan);
        String token = tokenService.issue(zip.path(), zip.fileName());
        GenerationSummaryDto summaryDto = new GenerationSummaryDto(
                summary.isDryRun(),
                List.copyOf(summary.getCreated()),
                List.copyOf(summary.getOverwritten()),
                List.copyOf(summary.getBackedUp()),
                List.copyOf(summary.getSkipped()),
                summary.getTotal());
        return new GenerateResponse(token, zip.fileName(), summaryDto, plan.getFileCount(),
                System.currentTimeMillis() - start);
    }

    private Prepared prepare(String sessionId, List<String> tables, GeneratorConfigRequest request) {
        if (tables == null || tables.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请至少选择一张表");
        }
        DataSourceSession session = sessionService.require(sessionId);
        GeneratorConfig config = configFactory.create(request, session.getDatasource(), tables);
        List<TableModel> models = new MetadataReader(config).read();
        if (models.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "未读取到表结构，请重新选择表");
        }
        return new Prepared(config, models);
    }

    private String readExisting(Path target) {
        try {
            if (Files.size(target) > MAX_PREVIEW_BYTES) {
                return null;
            }
            return Files.readString(target);
        } catch (IOException e) {
            logger.warn("读取现有文件失败: {}", target, e);
            return null;
        }
    }

    private ZipFile exportZip(GenerationPlan plan) {
        try {
            Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "easyjava-console");
            Files.createDirectories(dir);
            String fileName = "easyjava-generated-" + ZIP_TIMESTAMP.format(LocalDateTime.now()) + ".zip";
            Path path = dir.resolve(UUID.randomUUID() + "-" + fileName);
            return new ZipFile(new ZipExporter().export(plan, path), fileName);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "创建 ZIP 失败: " + e.getMessage());
        }
    }

    private record Prepared(GeneratorConfig config, List<TableModel> tables) {
    }

    private record ZipFile(Path path, String fileName) {
    }
}
