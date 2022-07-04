package Page;

import Core.Wrapper.Driver;
import Core.Wrapper.Element;
import Models.GithubLoginPopup;

public class LoginPage {
    Element loginWithGitHubButton = new Element("//a[@ng-click='login.loginWithGithub()']");

    public TodoPage loginWithGitHub() {
        GithubLoginPopup loginPopup = new GithubLoginPopup();
        String loginPageHandle = Driver.getWindowHandle();
        loginWithGitHubButton.click();
        Driver.switchToOtherWindow();
        loginPopup.login();
        loginPopup.checkStateAuthorizationAndClickIfTrue();
        Driver.switchToWindow(loginPageHandle);
        return new TodoPage();
    }

    public void clickLoginWithGitHubButton() {
        GithubLoginPopup loginPopup = new GithubLoginPopup();
        String loginPageHandle = Driver.getWindowHandle();
        loginWithGitHubButton.click();
        Driver.switchToOtherWindow();
        Driver.waitForPageLoad();
        loginPopup.checkStateAuthorizationAndClickIfTrue();
        Driver.switchToWindow(loginPageHandle);
    }
}
