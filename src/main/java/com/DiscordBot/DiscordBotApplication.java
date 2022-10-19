package com.DiscordBot;

import com.DiscordBot.lava.LavaplayerAudioSource;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class DiscordBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscordBotApplication.class, args);
	}

	@Autowired
	private Environment env;

	@Bean
	@ConfigurationProperties("discord-api")
	public DiscordApi discordApi() {
		String token = env.getProperty("TOKEN");
		DiscordApi api = new DiscordApiBuilder()
				.setToken(token)
				.login()
				.join();

		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().equalsIgnoreCase(".play")) {
				ServerVoiceChannel channel = event.getMessageAuthor().getConnectedVoiceChannel().get();
				channel.connect().thenAccept(audioConnection -> {
					// Do stuff

					AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
					audioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
					AudioSourceManagers.registerRemoteSources(audioPlayerManager);

					AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
                    // Create an audio source and add it to the audio connection's queue
                    AudioSource source = new LavaplayerAudioSource(api, audioPlayer);
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
		});

		return api;
	}

}