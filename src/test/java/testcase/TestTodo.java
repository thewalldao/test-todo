package testcase;

import Core.Wrapper.Driver;
import Core.Wrapper.Element;
import Core.Wrapper.ShadowElement;
import Page.LoginPage;
import Page.TodoPage;
import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.function.Supplier;


public class TestTodo extends TestBase1 {
    private static Logger logger = LogManager.getLogger(TestTodo.class);

    @Test(description = "Todo Test")
    public void test() {
        LoginPage loginPage = new LoginPage();

        logger.info("Go to Login Page");
        String baseURL = "https://todo-list-login.firebaseapp.com/";
        Driver.get(baseURL);

        logger.info("Login With GitHub");
        TodoPage todoPage = loginPage.loginWithGitHub();

        logger.info("create 10 todo list");
        todoPage.createTenTodoList();

        logger.info("logout");
        loginPage = todoPage.logOut();

        logger.info("login again");
        loginPage.clickLoginWithGitHubButton();

        logger.info("delete todo list from five to ten");
        todoPage.deleteTodoListFormFiveToTen();

    }
}
