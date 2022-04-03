package mock;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

// 메일 전송 확인용 클래스
public class MockMailSender implements MailSender {
    // 메일전송이 되었는지 안 되었는지 저장하는 자료구조
    private final List<String> request = new ArrayList<>();

    public List<String> getRequest() {
        return request;
    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        // 간단하게 전송요청받은 이메일주소 저장
        request.add(simpleMailMessage.getTo()[0]);
    }

    @Override
    public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {

    }
}
