package pl.luxoft.qpr.bilykov.repository;import com.github.springtestdbunit.annotation.DatabaseSetup;import org.junit.Assert;import org.junit.Test;import org.springframework.beans.factory.annotation.Autowired;import pl.luxoft.qpr.bilykov.model.User;import ua.com.homebudget.DblIntegrationTest;import ua.com.homebudget.model.User;@DatabaseSetup("user.xml")public class UserRepositoryTest extends DblIntegrationTest {    @Autowired    UserRepository userRepository;    @Test    public void testFindById() throws Exception {        User user = userRepository.findOne(1);        Assert.assertNotNull(user);    }    @Test    public void testFindByUnexistingId() throws Exception {        User user = userRepository.findOne(1131);        Assert.assertNull(user);    }    @Test    public void testFindByEmail() throws Exception {        final String email = "weird/email@mail.com";        User user = userRepository.findByEmail(email);        Assert.assertNotNull(user);        Assert.assertEquals(email, user.getEmail());    }}