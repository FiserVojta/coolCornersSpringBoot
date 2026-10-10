package com.lonework.corners.tag.model;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Normalization rules for user-supplied tag names.
 * <p>
 * The display name keeps the user's casing but trims and collapses whitespace. The normalized name
 * is the identity used to deduplicate tags ("low cost", "low-cost" and "LowCost" all map to "lowcost").
 * Keep {@link #normalize(String)} in sync with the SQL backfill in V0.1.0.56.
 */
public final class TagNames {

    private TagNames() {
    }

    public static String displayName(String name) {
        if (name == null) {
            return null;
        }
        return name.strip().replaceAll("(?U)\\s+", " ");
    }

    public static String normalize(String name) {
        if (name == null) {
            return null;
        }
        String folded = Normalizer.normalize(name, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(folded.length());
        folded.codePoints()
                .filter(Character::isLetterOrDigit)
                .forEach(sb::appendCodePoint);
        return sb.toString();
    }
}
