package alexzahv.MediaPlayer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayerFrame extends JFrame {
	private boolean isPausePressed = false;
	private Player player;
	private File file;
	private FileInputStream fileInputStream;
	private long pauselength;
	private long songTotalLength;
	private JPanel controlsPanel;
	private JButton playButton;
	private JButton stopButton;
	private JButton pauseButton;

	public PlayerFrame() {
		super("Рома рюмка");
		initComponents();
	}

	private void initComponents() {

		setSize(250, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		stopButton = new JButton("Stop");
		stopButton.addActionListener(new StopListener());
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayListener());
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new PauseListener());
		controlsPanel = new JPanel();
		add(controlsPanel, BorderLayout.SOUTH);
		controlsPanel.setLayout(new GridLayout(0, 3));
		controlsPanel.add(stopButton);
		controlsPanel.add(playButton);
		controlsPanel.add(pauseButton);
		JMenuBar menuBar = new JMenuBar();
		JMenuItem menuItem = new JMenuItem("Open");
		menuItem.addActionListener(new OpenListener());
		JMenu menu = new JMenu("File");
		menu.add(menuItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void pauseMusic() {
		try {
			pauselength = fileInputStream.available();
			fileInputStream.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		player.close();
		isPausePressed = true;

	}

	private void stopMusic() {
		player.close();
		isPausePressed = false;
		try {
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void playMusic() {
		if (file == null)
			openFile();
		try {
			fileInputStream = new FileInputStream(file);
			if (isPausePressed)
				fileInputStream.skip(songTotalLength - pauselength);
			else
				fileInputStream.skip(0);
			isPausePressed = false;
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			player = new Player(new BufferedInputStream(fileInputStream, 100));
		} catch (JavaLayerException e1) {
			e1.printStackTrace();
		}
		Thread t = new Thread(() -> {
			try {
				player.play();
			} catch (Exception e1) {

				e1.printStackTrace();
			}
		});
		t.setDaemon(true);
		t.start();

	}

	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				file = fileChooser.getSelectedFile();
				fileInputStream = new FileInputStream(file);
				songTotalLength = fileInputStream.available();
			} catch (FileNotFoundException e1) {

				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			try {
				if (player != null)
					player.close();
				player = new Player(new BufferedInputStream(fileInputStream));
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
			Thread t = new Thread(() -> {
				try {
					player.play();
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			});
			t.setDaemon(true);
			t.start();
			isPausePressed = false;
		}
	}

	private class OpenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			openFile();

		}

	}

	private class PlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			playMusic();

		}

	}

	private class StopListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			stopMusic();
		}

	}

	private class PauseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pauseMusic();

		}

	}
}
