import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private client!: Client;
  private connected = false;

  connect(): void {
    if (this.connected) return;
    this.client = new Client({
      webSocketFactory: () => new SockJS(`${environment.apiUrl}/ws`),
      reconnectDelay: 5000,
    });
    this.client.activate();
    this.connected = true;
  }

  subscribeToSlots(doctorId: number): Observable<any> {
    return new Observable(observer => {
      let stompSub: StompSubscription | undefined;
      let timeoutId: ReturnType<typeof setTimeout> | undefined;

      const checkAndSubscribe = () => {
        if (this.client?.connected) {
          stompSub = this.client.subscribe(`/topic/slots/${doctorId}`, (msg: IMessage) => {
            observer.next(JSON.parse(msg.body));
          });
        } else {
          timeoutId = setTimeout(checkAndSubscribe, 500);
        }
      };

      checkAndSubscribe();

      return () => {
        clearTimeout(timeoutId);
        stompSub?.unsubscribe();
      };
    });
  }

  disconnect(): void {
    this.client?.deactivate();
    this.connected = false;
  }
}
