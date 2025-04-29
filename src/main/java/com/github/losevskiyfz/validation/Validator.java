package com.github.losevskiyfz.validation;

import java.util.List;

import static com.github.losevskiyfz.conf.AllowedCodesProvider.getAllowedCodes;

public class Validator {
    private static final List<String> ALLOWED_CODES = getAllowedCodes();
}
