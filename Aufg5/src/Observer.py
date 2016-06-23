import pykka


class Actor(pykka.ThreadingActor):
    def __init__(self, name, my_arg=None):
        self.name = name
        super(Actor, self).__init__()

    def on_receive(self, message):
        if isinstance(message, dict):
            for value in message.values():
                print(value)
                print(self.name)


class Master(pykka.ThreadingActor):

    def __init__(self):
        self.observers = []
        super(Master, self).__init__()

    def remove(self, actorRef):
        if actorRef in self.observers:
            self.observers.remove(actorRef)

    def add(self, actorRef):
        if actorRef not in  self.observers:
            self.observers.append(actorRef)

    def sentMessages(self, message):
        for observer in self.observers:
            observer.tell(message)


def main():
    actor1_ref = Actor.start("actor 1")
    actor2_ref = Actor.start("actor 2")
    master = Master()
    master.start()

    master.add(actor1_ref)
    master.add(actor2_ref)
    master.sentMessages({"test": "hallo"})                             #actor.tell only allows dictionaries as parameter

    master.remove(actor1_ref)
    master.sentMessages({"test": "Test"})

    actor1_ref.stop(block=False)
    actor2_ref.stop(block=False)
    master.stop()


if __name__ == "__main__":
    main()
