package testFactory;

import com.foameraserblue.connection.ConnectionMaker;
import com.foameraserblue.connection.MysqlConnection;
import com.foameraserblue.dao.UserDaoJdbc;
import com.foameraserblue.service.TxProxyFactoryBean;
import com.foameraserblue.service.UserService;
import com.foameraserblue.service.UserServiceImpl;
import com.foameraserblue.service.UserServiceTx;
import mock.DummyMailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

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
    public UserServiceImpl userServiceImpl() {

        return new UserServiceImpl(userDao(), mailSender());
    }

    @Bean
    public TxProxyFactoryBean userService() {
        return new TxProxyFactoryBean(userServiceImpl(), transactionManager(), "upgradeLevels", UserService.class);
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }
}
