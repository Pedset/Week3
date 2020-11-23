package com.jaliansystems.javadriver.examples.swing.ut;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;


public class LoginDialogTest {

    private static  LoginDialog 	 login;
    private static  WebDriver 		 driver;
    private static  WebElement 		 user, pass, loginBtn, cancelBtn;
    private static  WebDriverWait	 wait;
    private static  List<WebElement> textComponents;
    
    // I use Junit 5
    @BeforeAll
    public static void createInstance() throws Exception {
        login = new LoginDialog() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSuccess() {
            }
            @Override
            protected void onCancel() {
            }
        };
	    SwingUtilities.invokeLater(() -> login.setVisible(true));
	    driver = 		new JavaDriver(new JavaProfile(LaunchMode.EMBEDDED).setLaunchType(LaunchType.SWING_APPLICATION));
	    wait   = 		new WebDriverWait(driver, 10);
	   	textComponents = 	driver.findElements(By.className  (JTextComponent.class.getName()));
	   	user           =	driver.findElement (By.cssSelector("text-field"));
	   	pass           =	driver.findElement (By.cssSelector("password-field"));
	   	loginBtn       = 	driver.findElement (By.cssSelector("button[text='Login']"));
	   	cancelBtn      =	driver.findElement (By.cssSelector("button[text='Cancel']"));
    }
    
    @BeforeEach
    public void setup() throws Exception{
    	if(!login.isVisible()) SwingUtilities.invokeLater(() -> login.setVisible(true));
    }

    @AfterEach
    public void tearDown() throws Exception {
        if(login.isVisible()) SwingUtilities.invokeAndWait(() -> login.dispose());
    }
    
    @AfterAll
    public static void endingAll() throws Exception{
    	 if(driver != null) driver.quit();
    }

    @Test
    @DisplayName("Testing a successful login")
    public void loginSuccess() {
        user.sendKeys("bob");
        pass.sendKeys("secret");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        assertTrue(login.isSucceeded(), "Log in successful?");
        assertTrue(login.getSize() != null);
    }

    @Test
    @DisplayName("Testing a canceled login")
    public void loginCancel() {
        user.sendKeys("bob");
        pass.sendKeys("secret");
        cancelBtn.click();
        assertFalse(login.isSucceeded(), "Log in successful?");
    }

    @Test
    @DisplayName("Testing an invalid login")
    public void loginInvalid() throws InterruptedException {
        user.sendKeys("bob");
        pass.sendKeys("wrong");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        driver.switchTo().window("Invalid Login");
        driver.findElement(By.cssSelector("button[text='OK']")).click();
        driver.switchTo().window("Login");
        assertTrue(user.getText().isEmpty(), "Is username text field empty?");
        assertTrue(pass.getText().isEmpty(), "Is username text field empty?");
        
    }

    // If we're checking something is not equal to null we can use "assertNotNull"
    @Test
    @DisplayName("Testing if tooltiptexts are not null")
    public void checkTooltipText() {
         textComponents.stream().map(e->{return e.getAttribute("toolTipText");}).forEach(Assertions::assertNotNull);
    }
    
    
    
    // -----------------------------------------------Question 2---------------------------------------------------
    
    // New changes in the application:
    
    // 1. Red color text field after wrong input
    	  
    //    If you type in a wrong username (Whether you enter the wrong or correct password)
    //    both username and password text fields will be in red color after login attempt and it will be changed
    //	  back to white again after you start typing in.
    //	  If you get the username right and password wrong, only the password field will be red.
    
    
    // 2. White spaces in the fields shouldn't enable login button
    
    //	  Before, the Login button was disabled whenever both text fields(username & password) were "empty".
    // 	  Now I use regex which neither allows empty nor Strings that contains only white spaces.
    
    @Nested
    @DisplayName("Testing text field colors")
    class checkTextFieldColor{
    		@Test
    		 @DisplayName("Testing when both fields are wrong")
    	     void checkBothWrong() {
    	    	user.sendKeys("bob2");
    	        pass.sendKeys("wrong");
    	        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
    	        loginBtn.click();
    	        driver.switchTo().window("Invalid Login");
    	        driver.findElement(By.cssSelector("button[text='OK']")).click();
    	        driver.switchTo().window("Login");
    	        
    	       assertEquals(Color.red.toString(), Color.class.getName()+user.getAttribute("background"), "Are both colors red?");
    	       assertEquals(Color.red.toString(), Color.class.getName()+pass.getAttribute("background"), "Are both colors red?");
    	 }
    		@Test
    		@DisplayName("Testing when only password field is wrong")
    		 void checkPassWrong() {
    			 user.sendKeys("bob");
    		       pass.sendKeys("wrong");
    		       wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
    		       loginBtn.click();
    		       driver.switchTo().window("Invalid Login");
    		       driver.findElement(By.cssSelector("button[text='OK']")).click();
    		       driver.switchTo().window("Login");
    		       
    		       assertEquals(Color.white.toString(), Color.class.getName()+user.getAttribute("background"), "Are both colors white?");
    		       assertEquals(Color.red.toString()  , Color.class.getName()+pass.getAttribute("background"), "Are both colors red?");
    		}
    }
    
    
    @Test
    @DisplayName("Testing empty white spaces")
    public void checkEmptySpace() throws InterruptedException{
			Thread.sleep(1000);
			
            assertTrue(loginBtn.getAttribute("isEnabled").equals("false"), "Is the login button disabled?");
            // Should still be disabled after empty white spaces
            user.sendKeys("    ");
            pass.sendKeys("    ");
            
            Thread.sleep(500);
            
            assertTrue(loginBtn.getAttribute("isEnabled").equals("false"), "Is the login button disabled?");
            
	        user.sendKeys("bob");
	        pass.sendKeys("secret");
	        Thread.sleep(500);
	        
	        assertTrue(loginBtn.getAttribute("isEnabled").equals("true"), "Is the login button enabled?");
	      
    }
    
    // -----------------------------------------------Question 3(Bonus) ----------------------------------------------
    
    
    //build.gradle file under dependencies junit:4.12
    
    
    
    
    
    
    
    
    
    
    
}
