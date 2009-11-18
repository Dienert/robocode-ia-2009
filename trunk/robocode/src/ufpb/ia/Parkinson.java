package ufpb.ia;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class Parkinson extends AdvancedRobot {
	
	private boolean indoPraFrente = false;
	double forcaTiro = 2; // For�a do tiro padr�o
	double energiaAnteriorInimigo = 100; // Todo oponente come�a com 100 de energia.
	double valorFolga = 99999; // Valor alto para loops.
	double quantidadeTirosErrados = 0; // Quantidade de disparos errados efetuados.
	double direcaoMovimento = 1;
	double direcaoArma = 1;
	
	public void run() {
		colore(); // Se pintando para a guerra.
		setMaxVelocity(Rules.MAX_VELOCITY); // velocidade m�xima.
		setTurnGunRight(valorFolga); // Fica girando o canh�o para a direita.
		circula(); // Circulando para tentar encontrar inimigos parados.
	}
  
	private void circula() {
		indoPraFrente = true; // Indica que o rob� est� indo para a frente.
		setAhead(valorFolga); // Vai pra frente.
		setTurnRight(valorFolga); // Girando para a direita.
	}
	
	private void colore() {
		setBodyColor(Color.RED); // Cor do corpo.
		setGunColor(Color.WHITE); // Cor da arma.
		setRadarColor(Color.BLACK); // Cor do radar.
		setScanColor(Color.CYAN); // Cor do Scanner.
		setBulletColor(Color.WHITE); // Cor da bala.
	}
  
	private void calculaPotenciaTiro(ScannedRobotEvent e) {
		if (getEnergy() <= 20) forcaTiro = Rules.MAX_BULLET_POWER;
		else forcaTiro = 2;
	}
  
	public void onScannedRobot(ScannedRobotEvent e) {
		calculaPotenciaTiro(e);
		setTurnRight(e.getBearing() + 90 - (30 * direcaoMovimento)); // Se posiciona em angulo em rela�ao ao inimigo.
		double mudarEnergia = energiaAnteriorInimigo - e.getEnergy(); // Flag que indica se o inimigo atirou.
		if (mudarEnergia > 0 && mudarEnergia <= 3) { // Executado quando o inimigo atira. Inimigo perde de 0 a 3 pontos de energia.
			direcaoMovimento = -direcaoMovimento;
			setAhead((e.getDistance()/4 + 25) * direcaoMovimento); // Se move pra frente desviando.     
		}
		direcaoArma = -direcaoArma;
		setTurnGunRight(valorFolga * direcaoArma); // Reposicionando o canh�o.
		
		fire(forcaTiro);
		
		/*if (quantidadeTirosErrados < 5) {
			if (e.getDistance() <= 30) {
				fire(Rules.MAX_BULLET_POWER); // Atira com for�aa m�xima se o inimigo estiver perto.
			} else fire(forcaTiro) ;
		} else {
			quantidadeTirosErrados = 0; // Zera os erros.
			circula(); // Est� na hora de circular novamente.
		}*/
		
		energiaAnteriorInimigo = e.getEnergy(); // Verifica com quanto de energia ficou o inimigo.
		if (quantidadeTirosErrados >= 5) {
			quantidadeTirosErrados = 0; // Zera os erros
			circula();
		}
	}
	
	public void onBulletMissed(BulletMissedEvent e) {
		quantidadeTirosErrados++; // Conta a quantidade de tiros errados.
	}
	
	public void onBulletHit(BulletHitEvent e) {
		quantidadeTirosErrados = 0; // Zera a quantidade de erros.
	}
  
	public void onWin(WinEvent e) {
		setRadarColor(Color.RED); // Cor do radar.
		for (int i = 1; i <= 360; i++) {
			setAhead(100);
			setAhead(-100);
		}
	}
  
	public void onHitWall(HitWallEvent e) {
		if (indoPraFrente) {
			setBack(200); // Se bate na parede de frente d� r�
			indoPraFrente = false;
		} else {
			setAhead(200); // Se bate na parede de r� vai pra frente.
			indoPraFrente = true;
		}
		circula(); // Circula novamente
	}
	
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -90 && e.getBearing() <= 90) {
			setBack(200); // Se afasta pra tr�s.
			indoPraFrente = false;
		} else {
			setAhead(200); // Se afasta pra frente.
			indoPraFrente = true;
		}
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			if (getEnergy() > 30) fire (3);
			else if (getEnergy() > 15) fire(2);
			else fire(1);
		}
		circula(); // Circula novamente
	}
  
}
