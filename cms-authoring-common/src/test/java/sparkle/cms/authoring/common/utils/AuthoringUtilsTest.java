package sparkle.cms.authoring.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * AuthoringUtilsTest
 * Created by bazzoni on 19/05/2015.
 */
public class AuthoringUtilsTest {

    @Test
    public void testToPrettyFilenameURI() throws Exception {
        final String uri = AuthoringUtils.toPrettyFileURI("dalkjda  dalk akdj alk/ adsjlkd jak  kjaòdljkò sd.jpg");
        assertEquals("dalkjda_dalk_akdj_alk/_adsjlkd_jak_kjaodljko_sd.jpg", uri);
    }

    @Test
    public void testToPrettyURL() throws Exception {

    }

    @Test
    public void testAbbreviateHtml() throws Exception {

    }
}