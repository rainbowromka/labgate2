import React from "react";
import {makeStyles} from "@material-ui/core";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import TextField from "@material-ui/core/TextField";
import AddOutlinedIcon from '@material-ui/icons/AddOutlined';

const useStyles = makeStyles((theme) => ({
  addItem: {
    padding: theme.spacing(1),
    minWidth: "180px",
    backgroundColor: "#8fafaf",
  },
  formBackGround: {
    borderWidth: 2,
    borderRadius: 0,
    borderColor: "#016464",
    borderStyle: "solid",
    backgroundColor: "#d9e5e5",
  }
}));

const PostDriver = (props) => {
  const classes = useStyles();
  let drivers = props.drivers;

  const [open, setOpen] = React.useState(false);

  let onAddDriver = () => {
    props.addDriver()
    setOpen(false)
  }

  let onSetDriverName = (e) => {
    props.setDriverName(e.target.value);
  }

  let onSetDriverCode = (e) => {
    props.setDriverCode(e.target.value);
  }

  let onSetDriverType = (e) => {
    props.setDriverType(e.target.value);
  }

  const handleClickOpen = () => {
    setOpen(true);
  }

  const handleClose = () => {
    props.setDriverName("");
    props.setDriverCode("");
    props.setDriverType("");
    setOpen(false)
  }

  return <Grid item xs={12} lg={2} md={3} sm={6}>
    <Box
      className={classes.addItem}
      p={1}
      border={2}
      borderColor={"#016464"}
      textAlign={"center"}
      justifyContent={"space-between"}
    >
      <Box p={1} width="100%" flexShrink={1}>
        <IconButton onClick={handleClickOpen}><AddOutlinedIcon/></IconButton>
      </Box>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="form-dialog-title"
        PaperProps={{className: classes.formBackGround}}
      >
        <DialogTitle id="from-dialog-title">Создать драйвер</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            autoFocus
            margin="dense"
            id="name"
            label="Имя драйвера"
            onChange={onSetDriverName}
            value={drivers.name}
          />
          <TextField
            fullWidth
            margin="dense"
            id="code"
            label="Код драйвера"
            onChange={onSetDriverCode}
            value={drivers.code}
          />
          <TextField
            fullWidth
            margin="dense"
            id="type"
            label="Тип соединения."
            onChange={onSetDriverType}
            value={drivers.type}
          />

        </DialogContent>
        <DialogActions>
          <Button onClick={onAddDriver}>
            Создать
          </Button>
          <Button onClick={handleClose}>
            Отмена
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  </Grid>
}

export default PostDriver;