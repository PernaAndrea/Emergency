package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {
	
	//CODA DEGLI EVENTI 
	private PriorityQueue<Event> queue;
	
	//MODELLO DEL MONDO
	private List<Patient> patients;
	private int freeStudios ; // numero di studi liberi
	private PriorityQueue<Patient> waitingRoom;
	
	private Patient.ColorCode ultimoColore;
	
	//PARAMETRI DI INPUT
	private int totStudios = 3 ; //NS numero di studi medici che ho in totale
	
	private int numPatients = 120 ; //NP 
	private Duration T_ARRIVAL = Duration.ofMinutes(5);
	
	private Duration DURATION_TRIAGE = Duration.ofMinutes(5);
	private Duration DURATION_WHITE = Duration.ofMinutes(10);
	private Duration DURATION_YELLOW = Duration.ofMinutes(15);
	private Duration DURATION_RED = Duration.ofMinutes(30);
	
	private Duration TIMEOUT_WHITE = Duration.ofMinutes(60); //tempo che si stanca e o va a casa o muore se rosso prima
	private Duration TIMEOUT_YELLOW = Duration.ofMinutes(30);
	private Duration TIMEOUT_RED = Duration.ofMinutes(30);
	
	private LocalTime startTime = LocalTime.of(8, 00);
	private LocalTime endTime = LocalTime.of(20, 00);
	
	//PARAMETRI DI OUTPUT
	private int patientsTreated ;   //curati
	private int patientsAbandoned ; //abbandonati
	private int patientsDead ;   //morti
	
	//INIZIALIZZZA IL SIMULATORE e crea gli venti iniziali SEPARATO DAL METODO RUN CHE EFFETTUA LA SIMULAZIONE VERA E PROPRIA
	public void init() {
		//inizializza coda eventi
		this.queue = new PriorityQueue<>();
		
		//inizializza il modello del mondo
		this.patients = new ArrayList<>();
		this.waitingRoom = new PriorityQueue<>();
		this.freeStudios = this.totStudios ; // inizialmente sono tutti liberi 
		
		this.ultimoColore = ColorCode.RED;
		
		//inizializza i parametri di output
		this.patientsAbandoned = 0;
		this.patientsDead = 0;
		this.patientsTreated = 0;
		
		//inietta i pazienti ( eventi di input )
		LocalTime ora = this.startTime ; 
		int inseriti = 0; 
//		Patient.ColorCode colore  = ColorCode.WHITE;
		
		this.queue.add(new Event(ora,EventType.TICK,null));
		
		while( ora.isBefore(this.endTime) && inseriti<this.numPatients) {
			
			Patient p = new Patient(inseriti,ora,ColorCode.NEW);
			Event e = new Event(ora, EventType.ARRIVAL,p);
			
			this.queue.add(e);
			this.patients.add(p);
			
			inseriti++;
			ora = ora.plus(T_ARRIVAL);
	
			}
	}
	
	//METODO RUN ESEGUE LA SIMULAZIONE 
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
	}
	
	private void processEvent(Event e) {
		
		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
//		Patient.ColorCode colore = p.getColor();
		
		switch(e.getType()) {
		case ARRIVAL :
			this.queue.add(new Event(ora.plus(DURATION_TRIAGE),EventType.TRIAGE,p));
			break;
			
		case TRIAGE :
			p.setColor(prossimoColore());
			if(p.getColor().equals(Patient.ColorCode.WHITE)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_WHITE),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			}
			else if(p.getColor().equals(Patient.ColorCode.YELLOW)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_YELLOW),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			}
			else if(p.getColor().equals(Patient.ColorCode.RED)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_RED),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
			}
			break;
			
		case FREE_STUDIO :
			if(this.freeStudios==0)
				return;
			// Quale paziente ha diritto di entrare
			Patient primo = this.waitingRoom.poll();
			if(primo != null) {
				//ammetti il paziente nello studio
				if(primo.getColor().equals(ColorCode.WHITE))
				this.queue.add(new Event(ora.plus(DURATION_WHITE),EventType.TREATED,primo));
				if(primo.getColor().equals(ColorCode.YELLOW))
					this.queue.add(new Event(ora.plus(DURATION_YELLOW),EventType.TREATED,primo));
				if(primo.getColor().equals(ColorCode.RED))
					this.queue.add(new Event(ora.plus(DURATION_RED),EventType.TREATED,primo));
				primo.setColor(ColorCode.TREATING);
				this.freeStudios--;
			}
			break;
			
		case TIMEOUT :
			Patient.ColorCode colore = p.getColor();
			switch(colore) {
			
			case WHITE :
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.OUT);
				this.patientsAbandoned++;
				break;
				
			case YELLOW :
				this.waitingRoom.remove(p); // se cambio colore devo toglierlo e poi riaggiungerlo
				
				p.setColor(ColorCode.RED);
				this.queue.add(new Event(ora.plus(TIMEOUT_RED),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
				break;
				
			case RED :
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.BLACK);
				this.patientsDead++;
				break;
				
			default:
			}
			break;
			
		case TREATED : //il paziente è uscito
			this.patientsTreated++;
			p.setColor(ColorCode.OUT);
			this.freeStudios++;
			this.queue.add(new Event(ora,EventType.FREE_STUDIO,null));
			break;
			
		case TICK :
			
			if(this.freeStudios>0 && !this.waitingRoom.isEmpty())
				this.queue.add(new Event(ora, EventType.FREE_STUDIO, null)) ;
			if(ora.isBefore(this.endTime))
				this.queue.add(new Event(ora.plus(Duration.ofMinutes(5)), EventType.TICK, null));
			break;
		}
	}
	
	private Patient.ColorCode prossimoColore() {
		
		
		if(ultimoColore.equals(ColorCode.WHITE)) {
			ultimoColore  = ColorCode.YELLOW ;
		}else if(ultimoColore.equals(ColorCode.YELLOW)){
			ultimoColore = ColorCode.RED;
		}else {
			ultimoColore = ColorCode.WHITE;
		}
		return ultimoColore;
	}
	
	
	public int getPatientsTreated() {
		return patientsTreated;
	}
	public int getPatientsAbandoned() {
		return patientsAbandoned;
	}
	public int getPatientsDead() {
		return patientsDead;
	}
	public void setQueue(PriorityQueue<Event> queue) {
		this.queue = queue;
	}
	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	public void setFreeStudios(int freeStudios) {
		this.freeStudios = freeStudios;
	}
	public void setTotStudios(int totStudios) {
		this.totStudios = totStudios;
	}
	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}
	public void setT_ARRIVAL(Duration t_ARRIVAL) {
		T_ARRIVAL = t_ARRIVAL;
	}
	public void setDURATION_TRIAGE(Duration dURATION_TRIAGE) {
		DURATION_TRIAGE = dURATION_TRIAGE;
	}
	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}
	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}
	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}
	public void setTIMEOUT_WHITE(Duration tIMEOUT_WHITE) {
		TIMEOUT_WHITE = tIMEOUT_WHITE;
	}
	public void setTIMEOUT_YELLOW(Duration tIMEOUT_YELLOW) {
		TIMEOUT_YELLOW = tIMEOUT_YELLOW;
	}
	public void setTIMEOUT_RED(Duration tIMEOUT_RED) {
		TIMEOUT_RED = tIMEOUT_RED;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	
}
