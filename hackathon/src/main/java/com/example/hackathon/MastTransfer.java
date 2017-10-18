package com.example.hackathon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // This tells Hibernate to make a table out of this class
@Table(name="masttransfer")
public class MastTransfer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
     
    public Long id;
    
    @Column(name="PAN1")
    public Long pan1;
    
    @Column(name="PAN2")
    public Long pan2;
     
    @Column(name="TRANSAMT")
    public Integer transamt;
       
    @Column(name="STATUS")
    public String status;
    
    
    //no-args constructor
    public MastTransfer()
    {}
    
    public MastTransfer(Long pan1,Long pan2,Integer transamt,String status)
    
    {
    	
    	this.pan1 = pan1;
    	this.pan2 = pan2;
    	this.transamt = transamt;
    	this.status= status;	
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPan1() {
		return pan1;
	}

	public void setPan1(Long pan1) {
		this.pan1 = pan1;
	}
	public Long getPan2() {
		return pan2;
	}

	public void setPan2(Long pan2) {
		this.pan2 = pan2;
	}

	public Integer getTransamt() {
		return transamt;
	}

	public void setTransamt(Integer transamt) {
		this.transamt = transamt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

