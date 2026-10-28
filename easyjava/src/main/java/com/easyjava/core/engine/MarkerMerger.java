package com.easyjava.core.engine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 按 {@code // <easyjava:table name="xxx">} 标记块增量合并聚合文件。
 * 已有块保持不变，只追加本次新出现的表，避免覆盖用户对旧块的定制。
 */
public final class MarkerMerger {

    private static final Pattern BLOCK = Pattern.compile(
            "// <easyjava:table name=\"([^\"]+)\">[\\s\\S]*?// </easyjava:table>");

    private MarkerMerger() {
    }

    public static String merge(String existing, String generated) {
        Parts generatedParts = parse(generated);
        if (generatedParts.blocks().isEmpty()) {
            return generated;
        }
        Parts existingParts = parse(existing);
        if (existingParts.blocks().isEmpty()) {
            return generated;
        }
        Map<String, String> merged = new LinkedHashMap<>(existingParts.blocks());
        for (Map.Entry<String, String> entry : generatedParts.blocks().entrySet()) {
            merged.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return generatedParts.head() + String.join("\n", merged.values()) + generatedParts.tail();
    }

    private static Parts parse(String content) {
        Matcher matcher = BLOCK.matcher(content);
        Map<String, String> blocks = new LinkedHashMap<>();
        int headEnd = content.length();
        int tailStart = 0;
        boolean found = false;
        while (matcher.find()) {
            if (!found) {
                headEnd = matcher.start();
            }
            blocks.put(matcher.group(1), matcher.group());
            tailStart = matcher.end();
            found = true;
        }
        if (!found) {
            return new Parts(content, Map.of(), "");
        }
        return new Parts(content.substring(0, headEnd), blocks, content.substring(tailStart));
    }

    private record Parts(String head, Map<String, String> blocks, String tail) {
    }
}
