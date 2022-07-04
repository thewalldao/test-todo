package testcase;

import Core.Wrapper.Driver;
import Utils.Constant;
import org.testng.annotations.*;

public class TestBase1 {

    @Parameters({"browser", "remote"})
    @BeforeMethod(alwaysRun = true)
    public void LaunchApplication(@Optional(Constant.DEFAULT_BROWSER) String browser, @Optional(Constant.DEFAULT_REMOTE_STATE) boolean remote) {
        Driver.setDriver(browser, remote);
        Driver.setPageLoadTimeOut();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Driver.closeBrowser();
    }
}
