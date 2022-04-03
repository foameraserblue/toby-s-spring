package mock;

import com.foameraserblue.dao.UserDao;
import com.foameraserblue.domain.User;

import java.util.ArrayList;
import java.util.List;

// userDao Mock 객체
public class MockUserDao implements UserDao {
    // 레벨 업그레이드 후보 user 오브젝트 목록
    private List<User> users;
    // 업데이트 대상 오브젝트 저장
    private List<User> updated = new ArrayList<>();

    public MockUserDao(List<User> users) {
        this.users = users;
    }


    // 스텁 기능 제공
    @Override
    public List<User> getAll() {
        return this.users;
    }

    // 목 오브젝트 기능 제공
    @Override
    public void update(User user) {
        updated.add(user);
    }

    @Override
    public void add(User user) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
        throw new UnsupportedOperationException();
    }


    public List<User> getUsers() {
        return users;
    }

    public List<User> getUpdated() {
        return updated;
    }
}
