package com.delicap.schema

import java.lang.reflect.Array

enum FormatValueType {
    isBool(Boolean.class, null, "A boolean value (true or false)"),
    isFloat(Float.class, null, "A floating-point value (between -3.4 × 10^38 and +3.4 × 10^38)"),
    isUnsignedFloat(Float.class, null, ""),
    isInt(Integer.class, null, "An integral value (between -2,147,483,648 and 2,147,483,647)"),
    isUnsignedInt(Integer.class, null, ""),
    isPercentage(Float.class, null, ""),
    isDate(Date.class, null, ""),
    isDateOrNull(Date.class, null, ""),
    isDateFormat(String.class, null, ""),
    isUnsignedId(Integer.class, null, "An integral and unsigned value (between 0 and 4294967296)"),
    isNullOrUnsignedId(Integer.class, null, "An integral and unsigned value (between 0 and 4294967296), or a NULL value"),
    isSerializedArray(Array.class, '/^a:[0-9]+:{.*;}\$/s', "PHP serialized data"),
    isString(String.class, null, "A string of characters"),
    isBirthDate(Date.class, '/^([0-9]{4})-((0?[1-9])|(1[0-2]))-((0?[1-9])|([1-2][0-9])|(3[01]))( [0-9]{2}:[0-9]{2}:[0-9]{2})?\$/', "A valid date, in YYYY-MM-DD format"),
    isCleanHtml(String.class, null, "Must not contain invalid HTML tags, nor XSS"),
    isColor(String.class, '/^(#[0-9a-fA-F]{6}|[a-zA-Z0-9-]*)\$/', "A valid HTML/CSS color, in #xxxxxx format or text format"),
    isEmail(String.class, '/^[a-z0-9!#$%&\\\'*+\\/=?^`{}|~_-]+[.a-z0-9!#$%&\\\'*+\\/=?^`{}|~_-]*@[a-z0-9]+[._a-z0-9-]*\\.[a-z0-9]+$/ui', "A valid e-mail address"),
    isImageSize(Integer.class, '/^[0-9]{1,4}$/', "A valid image size, between 0 and 9999"),
    isLanguageCode(Locale.class, '/^[a-zA-Z]{2}(-[a-zA-Z]{2})?$/', "A valid language code, in XX or XX-XX format"),
    isLanguageIsoCode(Locale.class, '/^[a-zA-Z]{2,3}$/', "A valid ISO language code, in XX or XXX format"),
    isLocale(Locale.class, null, ""),
    isMd5(String.class, '/^[a-f0-9A-F]{32}$/', "A valid MD5 string: 32 characters, mixing lowercase, uppercase and numerals"),
    isNumericIsoCode(String.class, '/^[0-9]{2,3}$/', "A valid ISO code, in 00 or 000 format"),
    isPasswd(String.class, /\/^[.a-zA-Z_0-9-!@#\u0024%\^&*()]{5,32}\u0024\//, "A valid password, in. between 5 and 32 characters long"),
    isPasswdAdmin(String.class, /\/^[.a-zA-Z_0-9-!@#\u0024%\^&*()]{8,32}\u0024\//, "A valid password, between 8 and 32 characters long"),
    isPhpDateFormat(Date.class, '/^[^<>]+$/', "A valid PHP date – in fact, a string without '<' nor '>'"),
    isPriceDisplayMethod(String.class, null, "A valid price display method, meaning the value be equals to constants PS_TAX_EXC or PS_TAX_INC"),
    isReference(String.class, '/^[^<>;={}]*$/u', "A valid product reference"),
    isUrl(URL.class, /\/^[~:#,%&_=\(\)\.\? \+\-@\\/a-zA-Z0-9]+\u0024\//, "A valid URL"),
    isCatalogName(String.class, '/^[^<>;=#{}]*$/u', "A valid product or category name"),
    isCarrierName(String.class, '/^[^<>;=#{}]*$/u', "A valid carrier name"),
    isConfigName(String.class, '/^[a-zA-Z_0-9-]+$/', "A valid configuration key"),
    isGenericName(String.class, '/^[^<>;=#{}]*$/u', "A valid standard name"),
    isStockManagement(String.class, null, ""),
    isJson(String.class, null, ""),
    isThemeName(String.class, null, ""),
    isReductionType(String.class, null, ""),
    isImageTypeName(String.class, '/^[a-zA-Z0-9_ -]+$/', "A valid image type"),
    isName(String.class, '/^[^0-9!<>,;?=+()@#"°{}_$%:]*$/u', "A valid name"),
    isTplName(String.class, '/^[a-zA-Z0-9_-]+$/', "A valid template name"),
    isAddress(String.class, '/^[^!<>?=+@{}_$%]*$/u', "A valid postal address"),
    isCityName(String.class, '/^[^!<>;?=+@#"°{}_$%]*$/u', "A valid city name"),
    isCoordinate(String.class, /\/^\-?[0-9]{1,8}\.[0-9]{1,8}\u0024\/s/, "A valid latitude-longitude coordinates, in 00000.0000 form"),
    isMessage(String.class, '/[<>{}]/i', "A valid message"),
    isPhoneNumber(String.class, '/^[+0-9. ()-]*$/', "A valid phone number"),
    isPostCode(String.class, '/^[a-zA-Z 0-9-]+$/', "A valid postal code"),
    isStateIsoCode(String.class, '/^[a-zA-Z0-9]{2,3}((-)[a-zA-Z0-9]{1,3})?$/', "A valid state ISO code."),
    isZipCodeFormat(String.class, '/^[NLCnlc -]+$/', "A valid zipcode format"),
    isAbsoluteUrl(URL.class, /\/^https?:\\/\\/[:#%&_=\(\)\.\? \+\-@\\/a-zA-Z0-9]+\u0024\//, "A valid absolute URL"),
    isDniLite(String.class, '/^[0-9A-Za-z-.]{1,16}$/U', "A valid DNI (Documento Nacional de Identidad) identifier. Specific to Spanish shops"),
    isIsbn(String.class, null, ""),
    isEan13(String.class, '/^[0-9]{0,13}$/', "A valid barcode (EAN13)"),
    isLinkRewrite(String.class, '/^[_a-zA-Z0-9-]+$/', "A valid friendly URL"),
    isPrice(Float.class, null, "A valid price display method (either PS_TAX_EXC or PS_TAX_INC)"),
    isUpc(String.class, '/^[0-9]{0,12}$/', "A valid barcode (UPC)"),
    isNegativePrice(Float.class, null, ""),
    isProductVisibility(String.class, null, ""),
    isTrackingNumber(String.class, null, ""),
    isModuleName(String.class, null, ""),
    isAnything(String.class, null, ""),
    isSha1(String.class, null, ""),
    isIp2Long(String.class, null, ""),
    isSiret(String.class, null, ""),
    isApe(String.class, null, ""),

    private final String description
    private final Class<?> clazz
    private final String regex

    FormatValueType(Class<?> clazz, String regex, String description) {
        this.description = description
        this.regex = regex
        this.clazz = clazz
    }

    Class<?> getType() {
        return this.clazz
    }

    String getValidationRegex() {
        return this.regex
    }

    String getDescription() {
        return this.description
    }
}
