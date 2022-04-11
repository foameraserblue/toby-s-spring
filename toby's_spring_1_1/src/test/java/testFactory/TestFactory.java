package testFactory;

import com.foameraserblue.aop.NameMatchClassMethodPointcut;
import com.foameraserblue.aop.TransactionAdvice;
import com.foameraserblue.connection.ConnectionMaker;
import com.foameraserblue.connection.MysqlConnection;
import com.foameraserblue.dao.UserDaoJdbc;
import com.foameraserblue.service.UserServiceImpl;
import mock.DummyMailSender;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import testclass.TestUserServiceImpl;

import javax.sql.DataSource;

import static com.foameraserblue.studyinfo.DB_ID;
import static com.foameraserblue.studyinfo.DB_PASSWORD;

@Configuration
public class TestFactory {
    // UserDao 를 생성하는 책임
    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setJdbcTemplate(dataSource());

        return userDaoJdbc;
    }

//    // AccountDao 를 생성하는 책임
//    @Bean
//    public AccountDao accountDao() {
//        return new AccountDao();
//    }

    // db 연결을 위한 데이터소스 빈 생성
    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/toby_1");
        dataSource.setUsername(DB_ID);
        dataSource.setPassword(DB_PASSWORD);

        return dataSource;
    }

    // 실제로 디비에 접속하게해주는 MysqlConnection 를 생성
    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new MysqlConnection();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public UserServiceImpl userService() {

        return new UserServiceImpl(userDao(), mailSender());
    }

//    // 프록시 팩토리 빈, 빈으로 등록
//    @Bean
//    public ProxyFactoryBean userService() {
//        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
//        proxyFactoryBean.setTarget(userServiceImpl());
//
//        String[] adviseAndAdvisor = {"transactionAdvisor"};
//        // 여러개의 어드바이서를 등록할 수 있다.
//        proxyFactoryBean.setInterceptorNames(adviseAndAdvisor);
//
//        return proxyFactoryBean;
//    }

//    @Bean
//    public TxProxyFactoryBean userService() {
//        return new TxProxyFactoryBean(userServiceImpl(), transactionManager(), "upgradeLevels", UserService.class);
//    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    // 어드바이스 빈 등록
    @Bean
    public MethodInterceptor transactionAdvise() {
        return new TransactionAdvice(transactionManager());
    }

    // 포인트컷 빈 등록
    @Bean
    public NameMatchClassMethodPointcut transactionPointcut() {
        NameMatchClassMethodPointcut namePointcut = new NameMatchClassMethodPointcut();
        namePointcut.setMappedName("*Levels");
        namePointcut.setMappedClassName("*ServiceImpl");
        return namePointcut;
    }

    // 어드바이서 빈 등록
    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvise());
    }
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    // 테스트용 스테틱 객체를 빈으로 등록
    @Bean
    public TestUserServiceImpl testUserService(){
        return new TestUserServiceImpl(userDao(),mailSender());
    }
}
