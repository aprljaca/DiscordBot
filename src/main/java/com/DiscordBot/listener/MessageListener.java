package com.DiscordBot.listener;

import com.DiscordBot.lava.LavaplayerAudioSource;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import lombok.RequiredArgsConstructor;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListener implements MessageCreateListener {

    @Autowired
    private PlayerManager playerManager;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().equalsIgnoreCase(".play")) {
            ServerVoiceChannel channel = event.getMessageAuthor().getConnectedVoiceChannel().get();
            channel.connect().thenAccept(audioConnection -> {
                System.out.println(audioConnection.getChannel().getName());
                // Create an audio source and add it to the audio connection's queue

                AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
                audioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
                AudioSourceManagers.registerRemoteSources(audioPlayerManager);

                AudioPlayer audioPlayer = audioPlayerManager.createPlayer();

                AudioSource source = new LavaplayerAudioSource(playerManager.getApi(), audioPlayer);
                audioConnection.setAudioSource(source);

                // You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,
                audioPlayerManager.loadItem("https://radiomiljacka-bhcloud.radioca.st/stream.mp3", new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        audioPlayer.playTrack(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            audioPlayer.playTrack(track);
                        }
                    }

                    @Override
                    public void noMatches() {
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                    }
                });

            }).exceptionally(e -> {
                // Failed to connect to voice channel (no permissions?)
                e.printStackTrace();
                return null;
            });
        }
    }

}

