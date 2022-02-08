import react from "react";
import PostDriver from "./PostDriver";
import {inject, observer} from "mobx-react";
import {AppStoreClass} from "../../../../state";

interface InjectedProps {
    driversStore: AppStoreClass
}

@inject("driversStore")
@observer
class PostDriverContainer extends react.Component
{
    get injected() {
        return this.props as InjectedProps
    }

    componentDidMount()
    {
    }

    render() {
        const {driversStore} = this.injected;
        return (
            <PostDriver
                drivers={driversStore.drivers}
                addDriver={driversStore.addDriver}
                setDriverName={driversStore.setDriverName}
                setDriverCode={driversStore.setDriverCode}
                setDriverType={driversStore.setDriverType}
            />
        );
    }
}

export default PostDriverContainer;