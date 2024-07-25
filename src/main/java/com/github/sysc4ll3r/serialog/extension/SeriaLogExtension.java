package com.github.sysc4ll3r.serialog.extension;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.persistence.Preferences;
import com.github.sysc4ll3r.serialog.tab.MainTab;

import java.text.MessageFormat;

public class SeriaLogExtension implements BurpExtension {
    private static final String NAME = "SeriaLog";
    private static final String VERSION = "V1.0";
    private static final String AUTHOR = "Sysc4ll3r";
    public static MontoyaApi montoyaApi;
    public static Preferences props;

    public void initialize(MontoyaApi api) {
        montoyaApi = api;
        props = montoyaApi.persistence().preferences();
        montoyaApi.extension().setName(SeriaLogExtension.NAME);
        montoyaApi.logging().logToOutput(MessageFormat.format("{0} {1} - By {2}", NAME, VERSION, AUTHOR));
        defaultConfig();
        montoyaApi.userInterface().registerSuiteTab(NAME, new MainTab());
        montoyaApi.extension().registerUnloadingHandler(System::gc);
    }

    private void defaultConfig() {
        if (props.getString(NAME) == null) {
            props.setString(NAME, VERSION);
            props.setBoolean("fileModeSer", true);
            props.setBoolean("fileModeXml", false);
            props.setBoolean("fileModeJson", false);
            props.setBoolean("fileFilterInScope", true);
            props.setBoolean("fileFilterSitemap", true);
            props.setBoolean("fileFilterProxy", true);
            props.setBoolean("fileFilterRepeater", true);
            props.setBoolean("fileFilterIntruder", true);
            props.setBoolean("fileFilterSequencer", true);
            props.setBoolean("fileFilterExtensions", true);
            props.setBoolean("fileFilterScanner", true);
            props.setInteger("fileFilterMaxResponse", 10);
            props.setBoolean("fileImportAppend", true);
            props.setBoolean("fileImportReplace", false);
            props.setBoolean("convertFromSer", false);
            props.setBoolean("convertFromXml", true);
            props.setBoolean("convertFromJson", false);
            props.setBoolean("convertToSer", true);
            props.setBoolean("convertToXml", false);
            props.setBoolean("convertToJson", false);

            props.setBoolean("loggingFilterInScope", true);
            props.setBoolean("loggingFilterProxy", false);
            props.setBoolean("loggingFilterRepeater", false);
            props.setBoolean("loggingFilterIntruder", false);
            props.setBoolean("loggingFilterSequencer", false);
            props.setBoolean("loggingFilterExtensions", false);
            props.setBoolean("loggingFilterScanner", false);

            props.setBoolean("filterInScope", true);
            props.setBoolean("filterWithoutResponses", false);
            props.setBoolean("filterParamItems", false);
            props.setBoolean("filterStatus1xx", true);
            props.setBoolean("filterStatus2xx", true);
            props.setBoolean("filterStatus3xx", true);
            props.setBoolean("filterStatus4xx", true);
            props.setBoolean("filterStatus5xx", true);
            props.setString("filterSearchText", "");
            props.setBoolean("filterSearchRegex", false);
            props.setBoolean("filterCaseSensitiveSearch", false);
            props.setBoolean("filterNegativeSearch", false);
            props.setBoolean("filterHtml", true);
            props.setBoolean("filterScript", true);
            props.setBoolean("filterXml", true);
            props.setBoolean("filterJson", true);
            props.setBoolean("filterCss", false);
            props.setBoolean("filterFlash", false);
            props.setBoolean("filterOtherText", true);
            props.setBoolean("filterOtherBinary", false);
            props.setBoolean("filterImages", false);
            props.setBoolean("filterProxy", true);
            props.setBoolean("filterRepeater", true);
            props.setBoolean("filterIntruder", true);
            props.setBoolean("filterSequencer", true);
            props.setBoolean("filterExtensions", true);
            props.setBoolean("filterScanner", true);
            props.setBoolean("filterShowOnlyExt", false);
            props.setBoolean("filterHideExt", false);
            props.setString("filterShowOnlyExtBy", "asp,aspx,jsp,php");
            props.setString("filterHideExtBy", "gif,jpg,png,ico,css,otf,ttf,woff2");
            props.setBoolean("filterNotesItems", false);
            props.setBoolean("filterAnnotatedItems", false);
            props.setString("lastUsedPath", System.getProperty("user.home"));
        }
    }
}
