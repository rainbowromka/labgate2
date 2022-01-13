import react, {FC, MouseEventHandler} from 'react';

import {makeStyles} from "@material-ui/core";

import LoadingButton from "@mui/lab/LoadingButton";
import TabContext from "@mui/lab/TabContext";
import TabList from    "@mui/lab/TabList";
import TabPanel from   "@mui/lab/TabPanel";

import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Grid from "@mui/material/Grid";
import Tab from "@mui/material/Tab";
import TextField from "@mui/material/TextField";

import FingerprintIcon from "@mui/icons-material/Fingerprint";

import PasswordInput from "../Commons/PasswordInput/PasswordInput";
import {AuthState} from "../../redux/auth-reducer";

/**
 * Стили формы авторизации.
 */
const useStyles = makeStyles((theme)=>({
  loginContent: {
    background: "#ffffff",
  },
  centerContent: {
      justifyContent: 'center'
  }
}));

/**
 * Вид формы - вход в систему.
 */
const AUTH_MODE_LOGIN = 'login';

/**
 * Вид формы - регистрация в системе.
 */
const AUTH_MODE_SIGNUP = 'signup';

export type AuthData = {
  authMode: typeof AUTH_MODE_LOGIN | typeof AUTH_MODE_SIGNUP
  username: string,
  email: string,
  password: string,
  checkPassword: string,
  showPassword: boolean,
  signInDisabled: boolean,
}

type Props = {
  auth: AuthState
  signIn: (username: string, password: string) => void
  signUp: (authData: AuthData, callback: () => void) => void
}

/**
 * Функциональная компонента отображения формы авторизации/регистрации.
 *
 * @param props
 *        пропсы, передаваемые в компоненту.
 * @returns JSX элемент формы авторизации/регистрации.
 */
const Login: FC<Props> = (props) => {
  const classes = useStyles();

  let auth = props.auth;

  const [authForm, setAuthForm] = react.useState({
    authMode: AUTH_MODE_LOGIN,
    username: "",
    email: "",
    password: "",
    checkPassword: "",
    showPassword: false,
    signInDisabled: true,
  } as AuthData);

  /**
   * Обработчик события при смене вкладки в форме авторизации/регистрации.
   *
   * @param event
   *        объект события.
   * @param newValue
   *        выбранная вкладка.
   */
  const handleLoginChange = (event: react.SyntheticEvent, newValue: typeof AUTH_MODE_LOGIN | typeof AUTH_MODE_SIGNUP) => {
    let newAuthForm: AuthData = {
      ...authForm,
      authMode: newValue,
      password: "",
      checkPassword: ""
    };
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события на изменение имени пользователя.
   *
   * @param e
   *        объект события.
   */
  const onChangeUsername = (e: react.BaseSyntheticEvent) => {
    let newAuthForm = {...authForm, username: e.target.value};
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события на изменение электронной почты.
   *
   * @param e
   *        объект события.
   */
  const onChangeEmail = (e: react.BaseSyntheticEvent) => {
    let newAuthForm = {...authForm, email: e.target.value};
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события на изменение пароля.
   *
   * @param e
   *        объект события.
   */
  const onChangePassword = (e: react.BaseSyntheticEvent) => {
    let newAuthForm = {...authForm, password: e.target.value};
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события на изменение проверочного пароля.
   *
   * @param e
   *        объект события.
   */
  const onChangeCheckPassword = (e: react.BaseSyntheticEvent) => {
    let newAuthForm = {...authForm, checkPassword: e.target.value};
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события, переключающий отображение пароля.
   */
  const handleClickShowPassword = () => {
    let newAuthForm = {...authForm, checkPassword: "", showPassword: !authForm.showPassword};
    setAuthForm({...newAuthForm, signInDisabled: updateSignIn(newAuthForm)});
  }

  /**
   * Обработчик события, выполняющего либо регистрацию, либо авторизацию
   * пользователя в системе.
   */
  const signIn = () => {
    if (authForm.authMode === AUTH_MODE_LOGIN) {
      props.signIn(authForm.username, authForm.password);
    }
    else if (authForm.authMode === AUTH_MODE_SIGNUP) {
      props.signUp(authForm, () => {
        setAuthForm({...authForm, authMode: AUTH_MODE_LOGIN});
      });
    }
  }

  /**
   * Выполняет валидацию форму и в случае, если она неправильная, то возвращает
   * - true.
   *
   * @param newAuthForm
   *        объект формы авторизации.
   * @returns true если форма не прошла валидацию.
   */
  // TODO: Сделать валидацию правильную.
  const updateSignIn = (newAuthForm: AuthData): boolean => {
    return (
      !newAuthForm.username
      || (newAuthForm.authMode === AUTH_MODE_SIGNUP
        && (!newAuthForm.email
          || !newAuthForm.password
          || (!newAuthForm.showPassword
            && newAuthForm.password !== newAuthForm.checkPassword)))
      || (newAuthForm.authMode === AUTH_MODE_LOGIN
        && !newAuthForm.password
      )
    );
  }

  return <Grid container justifyContent="center">
    <Grid className={classes.loginContent} item xs={12} sm={8} md={6} lg={4} xl={3}>
      <TabContext value={authForm.authMode}>
        <Box sx={{borderBottom: 1
          , borderColor: "divider"
        }}>
          <TabList
            onChange={handleLoginChange}
            aria-label="Вид авторизации"
            centered
          >
            <Tab value={AUTH_MODE_LOGIN} label="Вход"/>
            <Tab value={AUTH_MODE_SIGNUP} label="Регистрация"/>
          </TabList>
        </Box>
        <TabPanel value={AUTH_MODE_LOGIN}>
          <TextField
            sx={{m: 1, width: '100%'}}
            fullWidth
            autoFocus
            margin="dense"
            label="Имя пользователя"
            onChange={onChangeUsername}
            value={authForm.username}
            disabled={auth.isFetching}
          />
          <PasswordInput
            label="пароль"
            password={authForm.password}
            onChangePassword={onChangePassword}
            showPassword={authForm.showPassword}
            onClickShowPassword={handleClickShowPassword}
            disabled={auth.isFetching}
          />
          <Box textAlign="center">
            <LoadingButton
              disabled={authForm.signInDisabled}
              loading={auth.isFetching}
              loadingPosition="start"
              sx={{m: 1}}
              variant="contained"
              startIcon={<FingerprintIcon />}
              size="large"
              onClick={signIn}
            >
              Вход
            </LoadingButton>
          </Box>
        </TabPanel>
        <TabPanel value={AUTH_MODE_SIGNUP} classes={{root: classes.centerContent}}>
          <TextField
            sx={{m: 1, width: '100%'}}
            fullWidth
            autoFocus
            margin="dense"
            label="Имя пользователя"
            onChange={onChangeUsername}
            value={authForm.username}
            disabled={auth.isFetching}
          />
          <TextField
            sx={{m: 1, width: '100%'}}
            fullWidth
            margin="dense"
            label="e-mail"
            onChange={onChangeEmail}
            value={authForm.email}
            disabled={auth.isFetching}
          />
          <PasswordInput
            label="пароль"
            password={authForm.password}
            onChangePassword={onChangePassword}
            showPassword={authForm.showPassword}
            onClickShowPassword={handleClickShowPassword}
            disabled={auth.isFetching}
          />
          {!authForm.showPassword && <PasswordInput
            label="подтвердить пароль"
            password={authForm.checkPassword}
            onChangePassword={onChangeCheckPassword}
            showPassword={authForm.showPassword}
            onClickShowPassword={handleClickShowPassword}
            disabled={auth.isFetching}
          />}
          <Box textAlign="center">
            <Button
              disabled={authForm.signInDisabled}
              sx={{m: 1}}
              variant="contained"
              startIcon={<FingerprintIcon />}
              size="large"
              onClick={signIn}
            >
              Регистрация
            </Button>
          </Box>
        </TabPanel>
      </TabContext>
    </Grid>
  </Grid>
}

export default Login;