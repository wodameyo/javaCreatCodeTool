package com.easyjava.server.controller;

import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.server.config.ConsoleProperties;
import com.easyjava.server.dto.ColumnsRequest;
import com.easyjava.server.dto.ColumnsResponse;
import com.easyjava.server.dto.ConnectionRequest;
import com.easyjava.server.dto.ConnectionTestResponse;
import com.easyjava.server.dto.DefaultsResponse;
import com.easyjava.server.dto.GenerateRequest;
import com.easyjava.server.dto.GenerateResponse;
import com.easyjava.server.dto.HealthResponse;
import com.easyjava.server.dto.PreviewRequest;
import com.easyjava.server.dto.PreviewResponse;
import com.easyjava.server.dto.SessionRequest;
import com.easyjava.server.dto.TableListResponse;
import com.easyjava.server.service.ConsoleMetadataService;
import com.easyjava.server.service.DataSourceSession;
import com.easyjava.server.service.DataSourceSessionService;
import com.easyjava.server.service.DownloadTokenService;
import com.easyjava.server.service.GenerationService;
import com.easyjava.server.service.GeneratorConfigFactory;
import com.easyjava.server.service.PathGuard;
import com.easyjava.server.web.ApiException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;

/**
 * 可视化控制台的 REST API。
 */
@RestController
@RequestMapping("/api/console")
public class ConsoleController {

    private static final MediaType ZIP_MEDIA_TYPE = MediaType.parseMediaType("application/zip");

    private final DataSourceSessionService sessionService;

    private final ConsoleMetadataService metadataService;

    private final GenerationService generationService;

    private final DownloadTokenService tokenService;

    private final GeneratorConfigFactory configFactory;

    private final PathGuard pathGuard;

    private final ConsoleProperties properties;

    public ConsoleController(DataSourceSessionService sessionService,
                             ConsoleMetadataService metadataService,
                             GenerationService generationService,
                             DownloadTokenService tokenService,
                             GeneratorConfigFactory configFactory,
                             PathGuard pathGuard,
                             ConsoleProperties properties) {
        this.sessionService = sessionService;
        this.metadataService = metadataService;
        this.generationService = generationService;
        this.tokenService = tokenService;
        this.configFactory = configFactory;
        this.pathGuard = pathGuard;
        this.properties = properties;
    }

    @PostMapping("/datasource/test")
    public ConnectionTestResponse testConnection(@RequestBody ConnectionRequest request) {
        return sessionService.test(request);
    }

    @PostMapping("/datasource/tables")
    public TableListResponse listTables(@RequestBody SessionRequest request) {
        DataSourceSession session = sessionService.require(request.sessionId());
        return new TableListResponse(session.getId(), metadataService.listTables(session));
    }

    @PostMapping("/datasource/columns")
    public ColumnsResponse listColumns(@RequestBody ColumnsRequest request) {
        DataSourceSession session = sessionService.require(request.sessionId());
        return new ColumnsResponse(metadataService.listColumns(session, request.tables(),
                parseDateType(request.dateType())));
    }

    @PostMapping("/generate/preview")
    public PreviewResponse preview(@RequestBody PreviewRequest request) {
        return generationService.preview(request);
    }

    @PostMapping("/generate/plan/diff")
    public PreviewResponse planDiff(@RequestBody PreviewRequest request) {
        return generationService.preview(request);
    }

    @PostMapping("/generate")
    public GenerateResponse generate(@RequestBody GenerateRequest request) {
        return generationService.generate(request);
    }

    @GetMapping("/generate/download/{token}")
    public ResponseEntity<Resource> download(@PathVariable("token") String token)
            throws java.io.IOException {
        DownloadTokenService.DownloadEntry entry = tokenService.consume(token);
        Resource resource = new FileSystemResource(entry.path());
        String disposition = ContentDisposition.attachment()
                .filename(entry.fileName(), StandardCharsets.UTF_8)
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .contentType(ZIP_MEDIA_TYPE)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/config/defaults")
    public DefaultsResponse defaults() {
        return new DefaultsResponse(configFactory.defaults(), pathGuard.allowedRoots(),
                properties.getSessionTtlMinutes(), properties.getVersion(), properties.getPhase());
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", properties.getVersion(), properties.getPhase(),
                Instant.now());
    }

    private static GeneratorConfig.DateType parseDateType(String value) {
        String normalized = value == null || value.isBlank() ? "LEGACY" : value.trim();
        try {
            return GeneratorConfig.DateType.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "日期类型必须是 LEGACY / LOCAL");
        }
    }
}
