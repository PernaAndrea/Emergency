package model;
import java.time.LocalTime;


public class Event implements Comparable<Event>{

	enum EventType { //cio che cusa una variazione dello stato del paziente
		ARRIVAL, //arriva un nuovo paziente e entra in triage
		TRIAGE, // finito il triage entra in sala d'attesa con un particolare colore
		TIMEOUT, // passa un certo tempo di attesa
		FREE_STUDIO, //si è liberato uno studio e qualcuno puo essere ammesso
		TREATED, // paziente curato !!
		TICK, // timer per controllare se ci sono studi liberi
	};
	
	private LocalTime time ; 
	private EventType type ;
	private Patient patient ;
	
	public Event(LocalTime time, EventType type, Patient patient) {
		super();
		this.time = time;
		this.type = type;
		this.patient = patient;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public int compareTo(Event other) {
		// TODO Auto-generated method stub
		return this.time.compareTo(other.time);
	}
/*
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	*/

	@Override
	public String toString() {
		return "Event " + time + "  " + type + "  " + patient ;
	}
}
