package me.TfT02.Assassin.runnables;

import me.TfT02.Assassin.util.PlayerData;

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