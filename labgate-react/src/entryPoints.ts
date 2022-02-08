import SockJS from "sockjs-client";
import {CompatClient, messageCallbackType, Stomp} from "@stomp/stompjs";
import Cookies from "universal-cookie";

export type EntrypointRegistration = {
    route: string
    callback: messageCallbackType
}

export class EntryPointConnectionClass {

    private stompClient: CompatClient | null;

    constructor()
    {
        this.stompClient = null;
        this.getStompClient();
    }

    private getStompClient(): CompatClient | null {
        let coookies = new Cookies();
        let token = coookies.get("Token")
        if (token) {
            console.log('Is Authorized: ' + token);
            if (!this.stompClient) {
                this.stompClient = Stomp.over(() => {
                    return new SockJS('http://localhost:3002/driverentrypoint', {}, {});
                });
                this.stompClient.connect({}, ()=>{});
            }
        }
        else {
            if (this.stompClient) {
                this.stompClient.disconnect();
            }
            this.stompClient = null;
        }
        return this.stompClient
    }

    public registerEntryPoints(
        registrations: Array<EntrypointRegistration>)
    {
        const stompClient = this.getStompClient();
        if (stompClient) {
            registrations.forEach((registration: EntrypointRegistration) => {
                stompClient.subscribe(registration.route, registration.callback)
            })
        }
    }
}

const EP_CONNECTION = new EntryPointConnectionClass();

export default EP_CONNECTION;
// export function registerEntryPoints(
//     registrations: Array<EntrypointRegistration>)
// {
//     let cookies = new Cookies();
//     let token = cookies.get("Token");
//     console.log('Is Authorized: ' + Boolean(token));
//     if (token) {
//         const stompClient = Stomp.over(() => {
//             const socks =
//                 new SockJS('http://localhost:3002/driverentrypoint', {}, {});
//             return socks;
//         });
//
//         stompClient.connect({
//         }, (frame: IFrame) => {
//             registrations.forEach(
//                 (registration: EntrypointRegistration) => {stompClient
//                     .subscribe(registration.route, registration.callback)
//                 }
//             )
//         })
//     }
// }
