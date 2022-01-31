import SockJS from "sockjs-client";
import {Client, IFrame, messageCallbackType, Stomp} from "@stomp/stompjs";
import {APP_STORE} from "./state";

type Registration = {
    route: string
    callback: messageCallbackType
}

export function registerEntryPoints(registrations: Array<Registration>)
{
    if (APP_STORE.auth.isAuthorized) {

        const stompClient = Stomp.over(()=>{
            const socks = new SockJS('http://localhost:3002/app/driverentrypoint',{},{});
            return socks;
        });

        console.log("APP_STORE.auth.principal.token="+APP_STORE.auth.principal.token);
        stompClient.connect({
            'X-Authorization': `${APP_STORE.auth.principal.type} ${APP_STORE.auth.principal.token}`,
        }, (frame: IFrame) => {
            console.log("Connected: frame")
            registrations.forEach(
                (registration: Registration) => {
                    stompClient.subscribe(registration.route, registration.callback)
                }
            )
        })
    }
}





