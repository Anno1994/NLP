import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by anatolij on 17.06.16.
 */
public class ObserverPattern {

    static abstract class Wrapper{
        private final ActorRef actor;
        public Wrapper(ActorRef actor) {
            this.actor = actor;
        }
        public ActorRef getActor(){
            return actor;
        }
    }

    static class AddWrapper extends Wrapper{
        public AddWrapper(ActorRef actor) {
            super(actor);
        }
    }

    static class RemoveWrapper extends Wrapper{
        public RemoveWrapper(ActorRef actor) {
            super(actor);
        }
    }


    public static class SubjectActor extends UntypedActor {
        private List<ActorRef> observers = new ArrayList<>();
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof AddWrapper) {
                observers.add(((AddWrapper) message).getActor());
            } else if(message instanceof RemoveWrapper) {
                observers.remove(((RemoveWrapper) message).getActor());
            } else if(message instanceof String){
                if(message == "notify"){
                    for(ActorRef actor : observers){
                        actor.tell(message, getSelf());
                    }
                } else {
                    System.out.println(message);
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static class ObserverActor extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            //hier könnte etwas sinnvolles passieren,
            //stattdessen printen wir nur...
            System.out.println(getSelf().path().name() + " received message!");
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("actors");
        ActorRef subjectActor = system.actorOf(Props.create(SubjectActor.class), "SubjectActor");

        ActorRef obs1 = system.actorOf(Props.create(ObserverActor.class), "obs1");
        ActorRef obs2 = system.actorOf(Props.create(ObserverActor.class), "obs2");

        subjectActor.tell(new AddWrapper(obs1), ActorRef.noSender());
        subjectActor.tell(new AddWrapper(obs2), ActorRef.noSender());

        subjectActor.tell("test1", ActorRef.noSender());
        subjectActor.tell("test2", ActorRef.noSender());
        subjectActor.tell("test3", ActorRef.noSender());
        subjectActor.tell("notify", ActorRef.noSender());

        subjectActor.tell(new RemoveWrapper(obs1), ActorRef.noSender());
        subjectActor.tell(new RemoveWrapper(obs2), ActorRef.noSender());

        subjectActor.tell("test1", ActorRef.noSender());
        subjectActor.tell("test2", ActorRef.noSender());
        subjectActor.tell("test3", ActorRef.noSender());
        subjectActor.tell("notify", ActorRef.noSender());
    }
}
