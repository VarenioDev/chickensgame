package org.vareniodev.mc.chickensgame;

public class playerStats {
	private int totalGames = 0;
    private int wins = 0;
    private int losses = 0;
    
    public int getTotalGames(){return totalGames;}
    public int getWins() {return wins;}
    public int getLosses() {return losses;}
    
    public void setTotalGames(int newValue) {totalGames = newValue;}
    public void setWins(int newValue) {wins = newValue;}
    public void setLosses(int newValue) {losses = newValue;}
}
