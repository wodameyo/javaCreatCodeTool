package com.easyjava.core.cli;

import com.easyjava.core.config.ConfigLoader;
import com.easyjava.core.config.GeneratorConfig;
import com.easyjava.core.engine.CodeGenerator;
import com.easyjava.core.engine.GenerationPlan;
import com.easyjava.core.engine.GenerationSummary;
import com.easyjava.core.engine.OutputWriter;
import com.easyjava.core.engine.ZipExporter;
import com.easyjava.core.metadata.MetadataReader;
import com.easyjava.core.model.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 代码生成器命令行入口。
 * <pre>
 * --config path        指定配置文件
 * --tables a,b         仅生成指定表（覆盖 include）
 * --policy BACKUP      覆盖策略 OVERWRITE/SKIP/BACKUP
 * --dry-run            仅打印计划，不写盘
 * --zip [path]         导出 ZIP
 * </pre>
 */
public final class GeneratorCli {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorCli.class);

    private static final DateTimeFormatter ZIP_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private GeneratorCli() {
    }

    public static void main(String[] args) {
        int code = run(args);
        if (code != 0) {
            System.exit(code);
        }
    }

    public static int run(String[] args) {
        try {
            execute(args);
            return 0;
        } catch (Exception e) {
            logger.error("代码生成失败: {}", e.getMessage(), e);
            return 1;
        }
    }

    private static void execute(String[] args) {
        Options options = Options.parse(args);
        GeneratorConfig config = options.configPath == null
                ? ConfigLoader.loadDefault() : ConfigLoader.load(options.configPath);
        GeneratorConfig effective = options.apply(config);

        logger.info("开始读取数据库元数据...");
        List<TableModel> tables = new MetadataReader(effective).read();
        if (tables.isEmpty()) {
            logger.warn("未匹配到任何表，请检查 datasource 与 table.include/exclude 配置");
            return;
        }
        logger.info("匹配到 {} 张表: {}", tables.size(),
                tables.stream().map(TableModel::getTableName).toList());

        GenerationPlan plan = new CodeGenerator(effective).generate(tables);
        logger.info("生成计划文件数: {}", plan.getFileCount());

        GenerationSummary summary = new OutputWriter().write(plan, options.dryRun);
        logger.info(summary.describe());

        if (options.zipPath != null) {
            new ZipExporter().export(plan, options.zipPath);
        }
    }

    private static final class Options {
        private Path configPath;
        private List<String> tables;
        private GeneratorConfig.OverwritePolicy policy;
        private boolean dryRun;
        private Path zipPath;

        private static Options parse(String[] args) {
            Options options = new Options();
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("--config".equals(arg)) {
                    options.configPath = Paths.get(requireValue(args, ++i, arg));
                } else if (arg.startsWith("--config=")) {
                    options.configPath = Paths.get(arg.substring("--config=".length()));
                } else if ("--tables".equals(arg)) {
                    options.tables = split(requireValue(args, ++i, arg));
                } else if (arg.startsWith("--tables=")) {
                    options.tables = split(arg.substring("--tables=".length()));
                } else if ("--policy".equals(arg)) {
                    options.policy = parsePolicy(requireValue(args, ++i, arg));
                } else if (arg.startsWith("--policy=")) {
                    options.policy = parsePolicy(arg.substring("--policy=".length()));
                } else if ("--dry-run".equals(arg)) {
                    options.dryRun = true;
                } else if ("--zip".equals(arg)) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                        options.zipPath = Paths.get(args[++i]);
                    } else {
                        options.zipPath = defaultZipPath();
                    }
                } else if (arg.startsWith("--zip=")) {
                    options.zipPath = Paths.get(arg.substring("--zip=".length()));
                } else {
                    throw new IllegalArgumentException("未知参数: " + arg);
                }
            }
            return options;
        }

        private GeneratorConfig apply(GeneratorConfig config) {
            GeneratorConfig result = config;
            if (tables != null && !tables.isEmpty()) {
                result = new GeneratorConfig(
                        result.getDatasource(), result.getOutput(),
                        new GeneratorConfig.Table(tables, result.getTable().getExclude(),
                                result.getTable().getIgnorePrefix()),
                        result.getPackages(), result.getBackend(), result.getFrontend(), result.getAuthor());
            }
            if (policy != null) {
                GeneratorConfig.Output output = result.getOutput();
                result = new GeneratorConfig(
                        result.getDatasource(),
                        new GeneratorConfig.Output(output.getBackendDir(), output.getResourcesDir(),
                                output.getFrontendDir(), policy),
                        result.getTable(), result.getPackages(), result.getBackend(),
                        result.getFrontend(), result.getAuthor());
            }
            return result;
        }

        private static String requireValue(String[] args, int index, String name) {
            if (index >= args.length || args[index].startsWith("--")) {
                throw new IllegalArgumentException("参数 " + name + " 缺少取值");
            }
            return args[index];
        }

        private static List<String> split(String value) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .toList();
        }

        private static GeneratorConfig.OverwritePolicy parsePolicy(String value) {
            try {
                return GeneratorConfig.OverwritePolicy.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("非法的覆盖策略: " + value, e);
            }
        }

        private static Path defaultZipPath() {
            return Paths.get("easyjava-generated-" + ZIP_TIMESTAMP.format(LocalDateTime.now()) + ".zip")
                    .toAbsolutePath();
        }
    }
}
