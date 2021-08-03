package com.holdbetter.stonks.viewmodel;

import android.util.Log;

import com.holdbetter.stonks.Credentials;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class Repository {
    Boolean isUSMarketCurrentlyOpen() {
        Log.d("CHECKING_FOR_USMARKET", Thread.currentThread().getName());
        String answerJson = null;
        try {
            answerJson = IOUtils.toString(Credentials.isUSMarketOpenURL(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Boolean.parseBoolean(answerJson);
    }
}
