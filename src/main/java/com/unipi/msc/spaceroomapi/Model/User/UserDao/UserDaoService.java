package com.unipi.msc.spaceroomapi.Model.User.UserDao;

import com.unipi.msc.spaceroomapi.Model.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDaoService {
    private final UserDaoRepository userDaoRepository;

    public Optional<UserDao> getLastToken(User u) {
        return userDaoRepository.findFirstByUserOrderByCreatedDesc(u);
    }

    public boolean isTokenEnable(String token) {
        UserDao userDao = userDaoRepository.findUserDaoByTokenOrderByCreatedDesc(token).orElse(null);
        if (userDao == null) return true;
        return userDao.getIsActive();
    }

    public void disableOldUsersToken(User user) {
        List<UserDao> userDaos = userDaoRepository.findAllByUserAndIsActiveEquals(user,true);
        userDaos.stream().filter(UserDao::getIsActive).forEach(userDao -> {
            userDao.setIsActive(false);
            userDaoRepository.save(userDao);
        });
    }
}
