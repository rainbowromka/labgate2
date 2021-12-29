import react from "react";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputAdornment from "@mui/material/InputAdornment";
import IconButton from "@mui/material/IconButton";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import Visibility from "@mui/icons-material/Visibility";

/**
 * Функциональная компонента реализации поля ввода пароля и переключения
 * отображения/скрытия пароля.
 *
 * @param props
 *        передаваемые пропсы.
 * @returns JSX-компонента поля ввода пароля.
 */
const PasswordInput = (props) =>
{
  /**
   * Событие на нажатие на элементе отображения/скрытия пароля.
   * @param event
   *        объект события.
   */
  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  }

  return <FormControl sx={{m: 1, width: '100%'}} variant="outlined">
    <InputLabel htmlFor="outlined-adornment-password">{props.label}</InputLabel>
    <OutlinedInput
      id="outlined-adornment-password"
      type={props.showPassword ? 'text' : 'password'}
      value={props.password}
      onChange={props.onChangePassword}
      endAdornment={
        <InputAdornment position="end">
          <IconButton
            aria-label="показать/скрыть пароль"
            onClick={props.onClickShowPassword}
            onMouseDown={handleMouseDownPassword}
            edge="end"
          >
            {props.showPassword ? <VisibilityOff/> : <Visibility/>}
          </IconButton>
        </InputAdornment>
      }
      label={props.label}
      disabled={props.disabled}
    />
  </FormControl>
}

export default PasswordInput;