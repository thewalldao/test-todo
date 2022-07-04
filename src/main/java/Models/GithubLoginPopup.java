package Models;

import Core.Wrapper.Element;
import Page.TodoPage;
import Utils.Constant;

public class GithubLoginPopup {
    private String userName;
    private String password;

    Element userNameField = new Element("//input[@id='login_field']");
    Element passWordField = new Element("//input[@id='password']");
    Element submitButton = new Element("//input[@type='submit'][@value='Sign in']");
    Element authorizeButton = new Element("//button[@id='js-oauth-authorize-btn']");

    public GithubLoginPopup(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public GithubLoginPopup() {
        this.userName = Constant.DEFAULT_GITHUB_USERNAME;
        this.password = Constant.DEFAULT_GITHUB_PASSWORD;
    }

    public TodoPage login() {
        userNameField.enter(userName);
        passWordField.enter(password);
        submitButton.click();
        return new TodoPage();
    }

    public void checkStateAuthorizationAndClickIfTrue() {
        if (this.authorizeButton.isDisplayed(5)) {
            this.authorizeButton.click();
        }
    }
}
