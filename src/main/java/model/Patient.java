package model;

import java.time.LocalTime;

public class Patient implements Comparable<Patient>{
	
	public enum ColorCode {
		NEW, 						// in triage
		WHITE,YELLOW,RED,BLACK,     //codici per la gravità del paziente
		TREATING, 					// dentro uno studio medico 
		OUT							//a casa ( abbandonato o curato) 
	}; 
	private int num;
	private LocalTime attivalTime; //tempo di arrivo del paziente
	private ColorCode color;
	
	public Patient(int num,LocalTime attivalTime, ColorCode color) {
		super();
		this.num = num;
		this.attivalTime = attivalTime;
		this.color = color;
	}

	public LocalTime getAttivalTime() {
		return attivalTime;
	}

	public void setAttivalTime(LocalTime attivalTime) {
		this.attivalTime = attivalTime;
	}

	public ColorCode getColor() {
		return color;
	}

	public void setColor(ColorCode color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Patient  " +num +"  "+ attivalTime + "  " + color ;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (num != other.num)
			return false;
		return true;
	}

	@Override
	public int compareTo(Patient other) {
		// TODO Auto-generated method stub
		if(this.color.equals(other.color))
			return this.attivalTime.compareTo(other.attivalTime);
		else if(this.color.equals(Patient.ColorCode.RED))
			return -1;
		else if(other.color.equals(Patient.ColorCode.RED))
			return +1;
		else if(this.color.equals(Patient.ColorCode.YELLOW)) // Y -W
			return -1;
		else          	// W - Y 
			return +1;
	}

	

	
}
