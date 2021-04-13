/*
 * Creates a Panel using Swing Components consisting of a CountDown Timer synchronized with the System Clock.
 * Displays the Time Counter, boxes to select hour, minutes, seconds and buttons for reset, start and pause.
 * Set a time using the hour, minutes and seconds boxes and the timer starts counting the set time down to zero when the start button is pressed. 
 * @author 04xRaynal
 */

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.Duration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CountDown_Timer extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	DecimalFormat formatter = new DecimalFormat("00");      //formats the decimal into string, single digit numbers get padded a left zero ie. int 6 gets formatted as 06, rest numbers as usual ie. 25 as 25, 999 as 999
	long hours, minutes, seconds;
	long inputTime, lastTickTime, runningTime, timeLeft;
	
	JLabel labelTime, h, min, sec;
	JComboBox<String> hourComboBox, minutesComboBox, secondsComboBox;
	JButton  reset, start, pause;
	
	Timer timer;                             //importing javax.swing.Timer class
	
	public CountDown_Timer() {
		try {   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   }
		catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}        //Refines the look of the ui
		
		hours = minutes = seconds = 0;
		
		Image icon = Toolkit.getDefaultToolkit().getImage("clock-icon.png").getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		setIconImage(icon);
		setTitle("CountDown Timer");
		setLayout(null);
		setSize(250, 300);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);				//Frame exit when close button is pressed
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	
	public static void main(String main[]) {
		new CountDown_Timer();
	}

	
	public void createAndShowGUI() {
		labelTime = new JLabel();             	//label that displays the time
		changeLabelTimer();						//initially displays 00:00:00
		labelTime.setFont(new Font("Arial", Font.PLAIN, 30));
		labelTime.setBounds(40, 5, 200, 80);
		add(labelTime);
		
		
		hourComboBox = new JComboBox<String>();                 //combobox to choose hour, taken only 0-24 hours here, can be extended 
		for(int i = 0; i <= 24; i++) {							//filling values
			hourComboBox.addItem(formatter.format(i));
		}
		hourComboBox.setBounds(25, 90, 60, 50);
		hourComboBox.addActionListener(this);
		hourComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
		((JLabel) hourComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);        //center aligning the combobox text, renderer is necessary for aligning the text in combobox
		add(hourComboBox);
		
		
		minutesComboBox = new JComboBox<String>();				//combobox to choose minutes
		for(int i = 0; i < 60; i++) {							//filling values
			minutesComboBox.addItem(formatter.format(i));
		}
		minutesComboBox.setBounds(85, 90, 60, 50);
		minutesComboBox.addActionListener(this);
		minutesComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
		((JLabel) minutesComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);      //center aligning the combobox text, renderer is necessary for aligning the text in combobox
		add(minutesComboBox);
		
		
		secondsComboBox = new JComboBox<String>();				//combobox to choose seconds
		for(int i = 0; i < 60; i++) {							//filling values
			secondsComboBox.addItem(formatter.format(i));
		}
		secondsComboBox.setBounds(145, 90, 60, 50);
		secondsComboBox.addActionListener(this);
		secondsComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
		((JLabel) secondsComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);			//center aligning the combobox text, renderer is necessary for aligning the text in combobox
		add(secondsComboBox);
		
		
		h = new JLabel("h");
		h.setBounds(50, 140, 20, 20);
		h.setFont(new Font("Arial", Font.ITALIC, 13));
		add(h);
		
		min = new JLabel("min");
		min.setBounds(105, 140, 40, 20);
		min.setFont(new Font("Arial", Font.ITALIC, 13));
		add(min);
		
		sec = new JLabel("sec");
		sec.setBounds(165, 140, 40, 20);
		sec.setFont(new Font("Arial", Font.ITALIC, 13));
		add(sec);
		
		
		Image resetImage = Toolkit.getDefaultToolkit().getImage("reset-icon.png").getScaledInstance(50, 50, Image.SCALE_SMOOTH);        //Scaled Instance sets the image width and height
		reset = new JButton(new ImageIcon(resetImage));
		reset.setBounds(33, 190, resetImage.getWidth(getParent()), resetImage.getHeight(getParent()));									//width and height of the button taken from the image, hence no blank spaces
		reset.setBorder(BorderFactory.createEmptyBorder());								//creates empty border
		reset.setContentAreaFilled(false);												//empty area around the image is not filled
		reset.addActionListener(this);
		reset.setEnabled(false);					//disabled
		add(reset);
		
		
		Image startImage = Toolkit.getDefaultToolkit().getImage("play-icon.png").getScaledInstance(50, 50, Image.SCALE_SMOOTH);			//Scaled Instance sets the image width and height
		start = new JButton(new ImageIcon(startImage));
		start.setBounds(92, 190, startImage.getWidth(getParent()), startImage.getHeight(getParent()));									//width and height of the button taken from the image, hence no blank spaces
		start.setBorder(BorderFactory.createEmptyBorder());								//creates empty border
		start.setContentAreaFilled(false);												//empty area around the image is not filled
		start.addActionListener(this);
		add(start);
		
		
		Image pauseImage = Toolkit.getDefaultToolkit().getImage("pause-icon.jpg").getScaledInstance(50, 50, Image.SCALE_SMOOTH);		//Scaled Instance sets the image width and height
		pause = new JButton(new ImageIcon(pauseImage));
		pause.setBounds(150, 190, pauseImage.getWidth(getParent()), pauseImage.getHeight(getParent()));									//width and height of the button taken from the image, hence no blank spaces
		pause.setBorder(BorderFactory.createEmptyBorder());								//creates empty border
		pause.setContentAreaFilled(false);												//empty area around the image is not filled
		pause.addActionListener(this);
		pause.setEnabled(false);                 	 //disabled
		add(pause);
	}
	
	
	public long inputTimeInMilliseconds() {
		/*
		 * To covert hour into milliseconds, multiply hour by (60 * 60 * 1000)
		 * convert minute into milliseconds, multiply minute by (60 * 1000)
		 * convert second into millisecond, multiply second by 1000  
		 */
		return (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000) + 1000;			//1000 millisecond is added as a buffer since the timer starts with a 1000ms delay
	}
	
	
	public void update() {
		Duration duration = Duration.ofMillis(timeLeft);								//Duration holds the amount of time
		
		hours = duration.toHours();									//gets the amount of hours from duration
		duration = duration.minusHours(hours);						//subtracts the hour value from duration
		minutes = duration.toMinutes();								//gets the amount of minutes from duration
		duration = duration.minusMinutes(minutes);					//subtracts the minutes value from duration
		seconds = duration.toMillis() / 1000;								//gets the amount of seconds from duration
	}
	
	
	public void changeLabelTimer() {
//		labelTime.setForeground(Color.BLACK);				
		labelTime.setText(formatter.format(hours) + " : " + formatter.format(minutes) + " : " + formatter.format(seconds));
	}
	
	
	public void reset() {
		try {
			Thread.sleep(1);
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		
		hours = minutes = seconds = 0;		//to reset the label to 00:00:00
		changeLabelTimer();					
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == hourComboBox) {
			hours = Integer.parseInt(hourComboBox.getItemAt(hourComboBox.getSelectedIndex()));       //Selected Number from ComboBox which is a string gets parsed as an integer
			changeLabelTimer();
		}
		
		if(e.getSource() == minutesComboBox) {
			minutes = Integer.parseInt(minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex()));		//Selected Number from ComboBox which is a string gets parsed as an integer
			changeLabelTimer();
		}
		
		if(e.getSource() == secondsComboBox) {
			seconds = Integer.parseInt(secondsComboBox.getItemAt(secondsComboBox.getSelectedIndex()));		//Selected Number from ComboBox which is a string gets parsed as an integer
			changeLabelTimer();
		}
		
		if(e.getSource() == start) {
			reset.setEnabled(true);  pause.setEnabled(true);					//enabling the disabled buttons
			start.setEnabled(false);											//disabling as to prevent running two timers for the same task
			hourComboBox.setEnabled(false);  minutesComboBox.setEnabled(false);  secondsComboBox.setEnabled(false);          //disabling combobox to prevent resetting the numbers, mid countdown
			
			inputTime = inputTimeInMilliseconds();						//time selected from the comboboxes are converted into milliseconds
			lastTickTime = System.currentTimeMillis();
			
			timer = new Timer(1000, new ActionListener() {                  //timer to countdown the set time, 1000 ms (1 second) delay
				
				@Override
				public void actionPerformed(ActionEvent e) {
					runningTime = System.currentTimeMillis() - lastTickTime;		//Calculates the time elapsed since the start button was clicked
					timeLeft = inputTime - runningTime;	
					
					update();
					changeLabelTimer();
					
					if(hours <= 0 && seconds <= 0 && minutes <= 0) {
						Toolkit.getDefaultToolkit().beep();					//for the beep noise when time label gets to 00:00:00
						timer.stop();										//timer is stopped
						start.setEnabled(true);
						pause.setEnabled(false);  reset.setEnabled(false);
						hourComboBox.setSelectedIndex(0);  minutesComboBox.setSelectedIndex(0);  secondsComboBox.setSelectedIndex(0);			//resetting comboboxes to their initial values
						hourComboBox.setEnabled(true);  minutesComboBox.setEnabled(true);  secondsComboBox.setEnabled(true);					//enabling comboboxes after timer is completed
					}
				}
			});

			timer.start();						//starting timer
		}
		
		if(e.getSource() == pause) {
			timer.stop();						//stopping timer
			pause.setEnabled(false);
			start.setEnabled(true);
		}
		
		if(e.getSource() == reset) {
			timer.stop();					//stopping timer
			reset();
			reset.setEnabled(false);  pause.setEnabled(false); 
			start.setEnabled(true);
			hourComboBox.setSelectedIndex(0);  minutesComboBox.setSelectedIndex(0);  secondsComboBox.setSelectedIndex(0);				//resetting comboboxes to their initial values
			hourComboBox.setEnabled(true);  minutesComboBox.setEnabled(true);  secondsComboBox.setEnabled(true);						//enabling comboboxes after timer is completed
		}
	}
	
}
