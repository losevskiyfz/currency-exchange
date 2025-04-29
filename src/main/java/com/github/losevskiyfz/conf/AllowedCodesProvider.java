package com.github.losevskiyfz.conf;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class AllowedCodesProvider {
    private static final String fileName = PropertiesProvider.get("currency.api.validation.code.file");
    private static final List<String> ALLOWED_CODES = loadAllowedCodes();

    private static List<String> loadAllowedCodes() {
        try {
            return List.copyOf(
                    Files.readAllLines(
                            Paths.get(
                                    Objects.requireNonNull(
                                                    Thread.currentThread().getContextClassLoader().getResource(
                                                            fileName
                                                    ))
                                            .toURI()
                            )
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllowedCodes() {
        return ALLOWED_CODES;
    }
}
