package com.easyjava.core.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkerMergerTest {

    private static final String HEAD = "const routes = [\n";

    private static final String TAIL = "\n]\n";

    @Test
    void keepsExistingBlockAndAppendsNewTable() {
        String existing = HEAD + block("user_info", "custom-user") + TAIL;
        String generated = HEAD + block("user_info", "generated-user")
                + block("order_info", "generated-order") + TAIL;

        String merged = MarkerMerger.merge(existing, generated);

        assertTrue(merged.contains("custom-user"));
        assertFalse(merged.contains("generated-user"));
        assertTrue(merged.contains("generated-order"));
        assertTrue(merged.indexOf("custom-user") < merged.indexOf("generated-order"));
    }

    @Test
    void returnsGeneratedWhenExistingHasNoMarkerBlock() {
        String generated = HEAD + block("user_info", "generated-user") + TAIL;

        assertEquals(generated, MarkerMerger.merge("no markers here", generated));
    }

    @Test
    void keepsExistingContentWhenAllTablesAlreadyMerged() {
        String existing = HEAD + block("user_info", "user-route") + TAIL;
        String generated = HEAD + block("user_info", "user-route") + TAIL;

        assertEquals(existing, MarkerMerger.merge(existing, generated));
    }

    @Test
    void returnsGeneratedWhenTemplateHasNoBlocks() {
        String generated = HEAD + TAIL;

        assertEquals(generated, MarkerMerger.merge(HEAD + block("user_info", "x") + TAIL, generated));
    }

    private static String block(String tableName, String route) {
        return "// <easyjava:table name=\"" + tableName + "\">\n"
                + "  { path: '/" + route + "' },\n"
                + "// </easyjava:table>\n";
    }
}
