import react from "react";
import PostDriver from "./PostDriver";
import {observer} from "mobx-react";
import {APP_STORE} from "../../../../state";

@observer
class PostDriverContainer extends react.Component
{

    componentDidMount()
    {
    }


    render() {
        return (
            <PostDriver
                drivers={APP_STORE.drivers}
                addDriver={APP_STORE.addDriver}
                setDriverName={APP_STORE.setDriverName}
                setDriverCode={APP_STORE.setDriverCode}
                setDriverType={APP_STORE.setDriverType}
            />
        );
    }
}

export default PostDriverContainer;