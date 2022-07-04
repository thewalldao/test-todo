package Page;

import Core.Wrapper.Element;
import com.github.javafaker.Faker;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Supplier;

public class TodoPage {
    Element addListButton = new Element("//button[normalize-space(.)='Add List']");
    Element inputField = new Element("//input[@ng-model='home.list']");
    Element deleteButtonList = new Element("//ul[@class='list-group']//div//button");
    Element signOutButton = new Element("//button[normalize-space(.)='Sign out']");
    Element todoLabel = new Element("//div[normalize-space(.)='Todo Lists']");
    Faker faker = new Faker();

    public void createTenTodoList() {
        todoLabel.waitForDisplay();
        Supplier<String> supplier = () -> faker.ancient().god() + " " + faker.animal().name();

        for (int i = 0; i < 10; i++) {
            inputField.enter(supplier.get());
            addListButton.click();
        }
    }

    public LoginPage logOut() {
        signOutButton.click();
        return new LoginPage();
    }

    public void deleteTodoListFormFiveToTen() {
        todoLabel.waitForDisplay();
        deleteButtonList.waitForClickableAllElements();
        List<WebElement> elementList = deleteButtonList.getElementList();

        for (int i = 4; i < 10; i++) {
            elementList.get(i).click();
        }
    }

}
