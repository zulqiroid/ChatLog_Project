package com.example.chatlog_project.AudioService;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioService {
    private Context context;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String currentUrl;
    private int pausePosition = 0;
    private OnPlayCallBack onPlayCallBack;

    public AudioService(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    public void playAudioFromURL(String url, OnPlayCallBack onPlayCallBack) {
        this.onPlayCallBack = onPlayCallBack;

        try {
            if (isPlaying && url.equals(currentUrl)) {
                // Pause if already playing the same audio
                pauseAudio();
                return;
            }

            if (!url.equals(currentUrl)) {
                // Reset if a different URL is provided
                stopAudio();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
                pausePosition = 0; // Reset pause position for a new audio
            } else {
                // Resume from pause position
                mediaPlayer.seekTo(pausePosition);
                mediaPlayer.start();
            }

            isPlaying = true;
            currentUrl = url;

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                currentUrl = null;
                pausePosition = 0;
                onPlayCallBack.onFinished();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pausePosition = mediaPlayer.getCurrentPosition(); // Save current position
            isPlaying = false;
        }
    }

    public void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPlaying = false;
            pausePosition = 0; // Reset pause position
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0; // Return 0 if not playing
        }
    }

    public boolean isPlaying(String url) {
        return mediaPlayer != null && mediaPlayer.isPlaying() && url.equals(currentUrl);
    }

    public interface OnPlayCallBack {
        void onFinished();
    }
}
