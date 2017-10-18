package com.example.hackathon;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hackathon.Merchant;
import com.example.hackathon.MastTran;
import com.example.hackathon.MastTransfer;
import com.example.hackathon.Transaction;
import com.example.hackathon.UserRepository;
import com.example.hackathon.MastTranRepository;
import com.example.hackathon.MastTransferRepository;
import com.example.hackathon.MerchantRepository;
import com.google.gson.Gson;


@Controller    // This means that this class is a Controller
 // This means URL's start with /demo (after Application path)
public class MyController {
	
	Calendar calendar = Calendar.getInstance();
	Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
	String currTimestamp=currentTimestamp.toString();
	
	Transaction t;
	Merchant m;
	MastTran mt;
	MastTransfer mtran;
	Transaction t1;
	TTSWatson tts=new TTSWatson();
	 String json="";
	
	@Autowired // This means to get the bean called userRepository
	           // Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;
	
	@Autowired
	private MerchantRepository merchRepository;
	
	@Autowired
	private MastTransferRepository masttransferRepository;
	
	@Autowired
	private MastTranRepository mastertransactionRepository;
	
	@RequestMapping(path="/demo/transactionview",method=RequestMethod.GET)
	public String transactionview() {
	return "transactionview";
}

	 @RequestMapping(value="/demo/transactionview")
	 @ResponseBody
	 public String addNewUser(@RequestParam("pan") String pan,@RequestParam("merchid") String merchid,@RequestParam("billamt1") String billamt1,@RequestParam("amtpaid") String amtpaid,Model model) {
		json="";
		Long    pan1=Long.valueOf(pan);
		Long    merid=Long.valueOf(merchid);
		Integer billamt=Integer.valueOf(billamt1);
		Integer billpd=Integer.valueOf(amtpaid);
		Integer pointfetched;
		Integer pointsgiven;
		Integer valuefetched;
		Integer valuegiven;
		Integer valuetobepaid;
		String message="";
		Float merrate;
		
		
		
		try {
			m = merchRepository.findBymerchid(merid);
			Float mrrate=m.getmrate().floatValue();
			merrate=(mrrate/10.0f);
			
		}
		catch(Exception e)
		{
			
			message="Merchant with the entered ID is not associated with Discover network";
			model.addAttribute("message",message);
			tts.playSound(message);
			json=createjson(model);
			return json;
			
		}
		
		
		try {
		      t = userRepository.findBypan(pan1);
		      
		      pointfetched=t.getpoints();
		      valuefetched=t.getvalue();
		      
		      if(billamt.intValue()==billpd.intValue())
		      {     
		    	    pointsgiven=(int) (billamt*merrate);
		    	    valuegiven=(Integer)((pointsgiven/10));
		    	    pointfetched=pointfetched + pointsgiven;
			        valuefetched=valuefetched+valuegiven;
		    	    t.setpoints(pointfetched);
					t.setvalue(valuefetched);
					userRepository.save(t);
					mt=new MastTran(pan1,merid,billamt,billpd,valuegiven,0,currTimestamp,"SUCCESSFUL");
					mastertransactionRepository.save(mt);
		    	    message="Your transaction has been completed successfully and "+ valuegiven + " $ have been credited to your digital wallet";
		    	    
		    	    model.addAttribute("message",message);
		    	    model.addAttribute("message1","alert alert-info");
		      }
		      
		      if(billpd.intValue()<billamt.intValue())
		      {valuetobepaid=billamt-billpd;
		       if(valuefetched.intValue()>=valuetobepaid.intValue())
		       {   
		    	    valuefetched=valuefetched-valuetobepaid;
		    	    pointsgiven=(int)(billamt*merrate);
		    	    valuegiven=(Integer)((pointsgiven/10));
		    	    pointfetched=pointfetched + pointsgiven;
			        valuefetched=valuefetched+valuegiven;
		    	    t.setpoints(pointfetched);
					t.setvalue(valuefetched);
					userRepository.save(t);
		    	    message="Your transaction has been completed successfully and "+ valuetobepaid +" $ have been deducted from your wallet";
		    	   
		    	    model.addAttribute("message",message);
		    	    model.addAttribute("message1","alert alert-info");
		    	}
		       else
		       {message= "Your wallet does not have sufficient balance";
		       model.addAttribute("message",message);
		       model.addAttribute("message1","alert alert-info");
		      }	   
		      }	
		      
		      if(billpd.intValue()>billamt.intValue())
		      {
		    	  message="The value filled for the amount to be paid by card exceeds the transaction amount. Please correct it.";
		    	  model.addAttribute("message",message);
		      }
		    }
		    catch (Exception e) {
		    	
				message="The card number as entered does not exist";
				model.addAttribute("message",message);
				model.addAttribute("message1","alert alert-info");
		    }
		tts.playSound(message);
		json=createjson(model);
		return json;
		
	}
	
	@RequestMapping(path="/demo/transferview",method=RequestMethod.GET)
	public String transferview() {
	return "transferview";
	}
	
	@RequestMapping(path="/demo/addmerchant",method=RequestMethod.GET)
	public String loadmerchant() {
	return "addmerchant";
	}
	
	 @RequestMapping(value="/demo/transferview")
	 @ResponseBody
	  public String updateUser(@RequestParam("pan1") String pan1,@RequestParam("pan2") String pan2,@RequestParam("amt") String amt,Model model) {
		 	json="";
		    Long pan11=Long.valueOf(pan1);
		    Long pan21=Long.valueOf(pan2);
		 	Integer valuefetched1;
			Integer valuefetched2;
			Integer valamt=Integer.valueOf(amt);
			
		try {
	    	
			t = userRepository.findBypan(pan11);
	    	valuefetched1=t.getvalue();
	    }
	    catch (Exception ex) {
	        String message="The card number " + pan1+ " does not exist";
	        model.addAttribute("message",message);
	        model.addAttribute("message1","alert alert-info");
	        tts.playSound(message);
	        json=createjson(model);
	    	return json;
	    }
        try {
	    	
			t1 = userRepository.findBypan(pan21);
	    	valuefetched2=t1.getvalue();
	    }
	    catch (Exception ex) {
	      
	      String message="The card number " + pan2+ " does not exist";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	    }
        if(valamt<valuefetched1)
        {	
        	valuefetched1=valuefetched1-valamt;
        	t.setvalue(valuefetched1);
        	userRepository.save(t);
			valuefetched2=valuefetched2+valamt;
        	t1.setvalue(valuefetched2);
        	userRepository.save(t1);
			String message="Your account has been debited by "+ amt + " $" +" and the recipient's account has been credited by " + valamt+" $" ;
			model.addAttribute("message",message);
			 model.addAttribute("message1","alert alert-info");
			tts.playSound(message);
			json=createjson(model);
			return json;
        }
	 else
	 {
		 String message="Your account does not have sufficient balance to transfer this amount. Your current balance is " + valuefetched1 + " $";
		 model.addAttribute("message",message);
		 model.addAttribute("message1","alert alert-info");
		 tts.playSound(message);
		 json=createjson(model);
		 return json;
	 }
        
	 }
	 
	 @RequestMapping(path="/demo/pointsview",method=RequestMethod.GET)
	 public String pointsview() {
	 return "pointsview";
	 }


	 @RequestMapping(value="/demo/pointsview")
	 @ResponseBody
	  public String checkpoints(@RequestParam("ypan") String pan,Model model) {
		json="";
		Long pan1=Long.valueOf(pan);
	    Integer valueinwallet;
	    try {
	      Transaction t = userRepository.findBypan(pan1);
	      valueinwallet = t.getvalue();
	    }
	    catch (Exception ex) {
	      String message="User not found";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	    }
	      String message="The total amount in your digtal wallet is : " + valueinwallet+" $";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	  }
	 
	 @RequestMapping(value="/demo/cardcheck")
	 @ResponseBody
	  public String pointalert(@RequestParam("ypan") String pan,Model model) {
		json="";
		Long pan1=Long.valueOf(pan);
	    Integer valueinwallet;
	    try {
	      Transaction t = userRepository.findBypan(pan1);
	      valueinwallet = t.getvalue();
	    }
	    catch (Exception ex) {
	      String message="The card number as entered does not exist";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      json=createjson(model);
	      return json;
	    }
	      String message="The total amount in the cardholder's digital wallet is : " + valueinwallet+" $";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      json=createjson(model);
	      return json;
	  }
	 
	 @RequestMapping(value="/demo/merchcheck")
	 @ResponseBody
	  public String merchalert(@RequestParam("merchid") String merchid,Model model) {
		json="";
		Long merid=Long.valueOf(merchid);
	    Float mrate;
	    
	    try {
	    	m = merchRepository.findBymerchid(merid);
	    	Float merrate=m.getmrate().floatValue();
			mrate=(merrate/100.0f);
	    }
	    catch (Exception ex) {
	      String message="Merchant with the entered ID is not associated with Discover network";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	    }
	      String message="The loyalty and rewards rate of the entered merchant is : " + mrate +" $";
	      model.addAttribute("message",message);
	      model.addAttribute("message1","alert alert-info");
	      json=createjson(model);
	      return json;
	  }
	 
	 @RequestMapping(value="/demo/merchview")
	 @ResponseBody
	  public String merchview(@RequestParam("merchid") String merchid,Model model) {
		json="";
		Long merid=Long.valueOf(merchid);
	    String mername="";
	    Integer merrate=0;
	    try {
	      Merchant m = merchRepository.findBymerchid(merid);
	      mername =m.getmerchname();
	      merrate=m.getmrate();    
	    }
	    catch (Exception ex) {

	    }

	      model.addAttribute("mername",mername);
	      model.addAttribute("merrate",merrate);
	      
	      json=createjson(model);
	      return json;
	  }
	 
	 @RequestMapping(path="/demo/addpan",method=RequestMethod.GET)
	 public String abc() {
	return "addpan";
	 }
	 
	 
	  @RequestMapping(value="/demo/addpan")
	  @ResponseBody
	  public String addnewpan(@RequestParam("pan") String pan,Model model){  
		  String message=" ";
		  json="";
		  Long pan1=Long.valueOf(pan);
		  try {
		      Transaction t = userRepository.findBypan(pan1);
		      t.getpan();
		      message="The card number already exists";
		      model.addAttribute("message",message);
		    }
		  catch(Exception e)
		  {t=new Transaction(pan1,0,0);
		  userRepository.save(t);
		  message="Your card has been successfully added to the loyalty rewards programme";
		  model.addAttribute("message",message);
		  model.addAttribute("message1","alert alert-info");
		  }
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	  }
	  
	  @RequestMapping(value="/demo/addmerchant")
	  @ResponseBody
	  public String addnewmerchant(@RequestParam("merchid") String merchid,@RequestParam("merchname") String merchname,@RequestParam("mrate") String mrate,Model model){  
		  String message=" ";
		  json="";
		  Long merid=Long.valueOf(merchid);
		  Integer merchrate=Integer.valueOf(mrate);
		  System.out.println(merchrate);
		  try {
		      m = merchRepository.findBymerchid(merid);
		      m.getmerchid();
		      m.setmerchid(merid);
		      m.setmerchname(merchname);
		      m.setmrate(merchrate);
		      merchRepository.save(m);
		      message="Merchant details have been successfully updated and now for every 100$ of transaction, user will get " + merchrate +" $";
		      model.addAttribute("message",message);
		    }
		  
		  catch(Exception e)
		  {m=new Merchant(merid,merchname,merchrate);
		  merchRepository.save(m);
		  message="Merchant details have been successfully added to the loyalty rewards programme and for every 100$ of transaction, user will get " + merchrate + " $";
		  model.addAttribute("message",message);
		  model.addAttribute("message1","alert alert-info");
		  }
	      tts.playSound(message);
	      json=createjson(model);
	      return json;
	  }
	  
	  @RequestMapping(path="/demo/transview",method=RequestMethod.GET)
		 public String viewtrans(Model model) {
		  List<Transaction> t3=userRepository.findAll();
		  model.addAttribute("transaction",t3);
		  return "transview";
		 }
	 
	  
	  public String createjson(Model model) 
	  {
		  try
	      {
	    	  Gson gson = new Gson();
	    	  json = gson.toJson(model);
	      }
	      catch(Exception e)
	      {}
	      return json;
		  
	  }
}
