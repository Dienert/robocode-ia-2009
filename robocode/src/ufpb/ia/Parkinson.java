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
	double forcaTiro = 2; // Força do tiro padrão
	double energiaAnteriorInimigo = 100; // Todo oponente começa com 100 de energia.
	double valorFolga = 99999; // Valor alto para loops.
	double quantidadeTirosErrados = 0; // Quantidade de disparos errados efetuados.
	double direcaoMovimento = 1;
	double direcaoArma = 1;
	
	public void run() {
		colore(); // Se pintando para a guerra.
		setMaxVelocity(Rules.MAX_VELOCITY); // velocidade máxima.
		setTurnGunRight(valorFolga); // Fica girando o canhão para a direita.
		circula(); // Circulando para tentar encontrar inimigos parados.
	}
  
	private void circula() {
		indoPraFrente = true; // Indica que o robô está indo para a frente.
		setAhead(valorFolga); // Vai pra frente.
		setTurnRight(valorFolga); // Girando para a direita.
	}
    /**
     * PALMEIRAAAASS!!!!
     *
	 **/
	private void colore() {
		setBodyColor(Color.GREEN); // Cor do corpo.
		setGunColor(Color.WHITE); // Cor da arma.
		setRadarColor(Color.BLACK); // Cor do radar.
		setScanColor(Color.CYAN); // Cor do Scanner.
		setBulletColor(Color.WHITE); // Cor da bala.
	}
  
	private void calculaPotenciaTiro(ScannedRobotEvent e) {
		if (getEnergy() <= 20 || e.getDistance() > 100) {
			forcaTiro = 2; // Se a energia eh baixa ou o inimigo esta distante, atira com menos forca.
		} else forcaTiro = Rules.MAX_BULLET_POWER; // Força maxima.
	}
  
	public void onScannedRobot(ScannedRobotEvent e) {
		calculaPotenciaTiro(e); // Escolher a melhor força para o tiro.
		setTurnRight(e.getBearing() + 90 - (30 * direcaoMovimento)); // Se posiciona em angulo em relaçao ao inimigo.
		double mudarEnergia = energiaAnteriorInimigo - e.getEnergy(); // Flag que indica se o inimigo atirou.
		if (mudarEnergia > 0 && mudarEnergia <= 3) { // Executado quando o inimigo atira. Inimigo perde de 0 a 3 pontos de energia.
			direcaoMovimento = -direcaoMovimento;
			setAhead((e.getDistance()/4 + 25) * direcaoMovimento); // Se move pra frente desviando.     
		}
		direcaoArma = -direcaoArma;
		setTurnGunRight(valorFolga * direcaoArma); // Reposicionando o canhão.
		if (quantidadeTirosErrados < 5) {
			if (e.getDistance() <= 30) {
				fire(Rules.MAX_BULLET_POWER); // Atira com forçaa máxima se o inimigo estiver perto.
			} else fire(forcaTiro) ;
		} else {
			quantidadeTirosErrados = 0; // Zera os erros.
			circula(); // Está na hora de circular novamente.
		}
		energiaAnteriorInimigo = e.getEnergy(); // Verifica com quanto de energia ficou o inimigo. 
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
			turnGunRight(i); // Fica girando o canhão em comemoração a vitória.
			turnLeft(i); // Gira o Corpo para esquerda.
		}
	}
  
	public void onHitWall(HitWallEvent e) {
		if (indoPraFrente) {
			setBack(200); // Se bate na parede de frente dá ré
			indoPraFrente = false;
		} else {
			setAhead(200); // Se bate na parede de ré vai pra frente.
			indoPraFrente = true;
		}
		circula(); // Circula novamente
	}
	
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -90 && e.getBearing() <= 90) {
			setBack(200); // Se afasta pra trás.
			indoPraFrente = false;
		} else {
			setAhead(200); // Se afasta pra frente.
			indoPraFrente = true;
		}
		circula(); // Circula novamente
	}
  
}
