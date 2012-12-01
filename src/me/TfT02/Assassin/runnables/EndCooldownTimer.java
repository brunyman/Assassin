package me.TfT02.Assassin.runnables;

import me.TfT02.Assassin.util.PlayerData;

public class EndCooldownTimer implements Runnable {
	private final String playerName; // We want the players name in this variable.
	public EndCooldownTimer(String playerName) // This is the constructor, it takes a String as a argument.
	{
		this.playerName = playerName; //Now we map the variable given to the constructor to the variable we want it to have.
	}

	@Override
	public void run() {
		updateCooldownList();
	}
	private void updateCooldownList() {
		PlayerData.playerCooldown.remove(playerName);
	}
}