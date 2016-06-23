require 'celluloid'

class Actor
  include Celluloid

  def initialize(name)
    @name = name
  end

  def onrecieve(message)
    if message.is_a? String
      puts message + @name
    end

  end

end


class Observer
  include Celluloid

  def initialize
    @observers = []
  end

  def add(observer)
    if !(@observers.include? observer)
      @observers.push(observer)
    end
  end

  def remove(observer)
    if @observers.include? observer
      @observers.delete(observer)
    end
  end

  def sentMessages(message)
    @observers.each { |obs|
      obs.onrecieve(message)
    }
  end

  def test
    print(@observers)
  end
end



Actor_1 = Actor.new("actor 1")
Actor_2 = Actor.new("actor 2")

Master = Observer.new()

Master.add(Actor_1)
Master.add(Actor_2)
Master.sentMessages("Hello ")

Master.remove Actor_1
Master.sentMessages("Hello again ")

