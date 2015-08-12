package com.george.redditreader.api.captcha;

import com.george.redditreader.api.utils.ApiEndpointUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by George on 8/11/2015.
 */
public class CaptchaDownloader {

    /**
     * Fetch a rendered image of the captcha that is identified by the iden given along.
     * @param iden Captcha identifier
     * @return Captcha rendered image
     * @throws IOException 	Thrown if the image retrieval failed
     */
    //public RenderedImage getCaptchaImage(String iden) throws IOException {
    //    URL url = new URL(ApiEndpointUtils.REDDIT_BASE_URL + "/captcha/" + iden + ".png");
    //    RenderedImage captcha = ImageIO.read(url);
    //    return captcha;
    //}

}
