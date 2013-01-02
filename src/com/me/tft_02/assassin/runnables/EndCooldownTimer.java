package com.me.tft_02.assassin.runnables;

import com.me.tft_02.assassin.util.PlayerData;

public class EndCooldownTimer implements Runnable {
	private final String playerName;

	public EndCooldownTimer(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void run() {
		updateCooldownList();
	}

	private void updateCooldownList() {
		PlayerData.playerCooldown.remove(playerName);
	}
}