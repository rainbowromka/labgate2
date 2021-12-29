package ru.idc.labgatej.manager.security.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idc.labgatej.manager.model.User;
import ru.idc.labgatej.manager.repo.UserRepository;

/**
 * Реализация основного интерфейса, загружающего пользовательские данные.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    UserRepository userRepository;

    /**
     * Получаем имя пользователя из репозитория и строим объект UserDetails.
     *
     * @param username
     *        имя пользователя.
     * @return объект детальной информации о пользователе.
     * @throws UsernameNotFoundException
     *         генерируемое исключение.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(
        String username)
    throws UsernameNotFoundException
    {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not Found with username " + username));
        return UserDetailsImpl.build(user);
    }
}
