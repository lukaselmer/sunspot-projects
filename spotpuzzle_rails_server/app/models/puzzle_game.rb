class PuzzleGame < ActiveRecord::Base
  named_scope :latest, :order => 'created_at DESC'
  named_scope :fastest, :limit => 3, :order => 'cycle_times ASC'
  named_scope :slowest, :limit => 3, :order => 'cycle_times DESC'
  named_scope :fewest_swaps, :limit => 3, :order => 'swap_times ASC'
  named_scope :most_swaps, :limit => 3, :order => 'swap_times DESC'

  def game_time
    seconds = (cycle_times * 250.0 / 1000.0)
    seconds = 234.3
    minutes = ((seconds - (seconds % 60)) / 60).to_i
    seconds -= minutes * 60
    [minutes, seconds].collect
  end

  def game_time_in_words
    arr = game_time
    ret = ""
    ret += "#{arr[0]} Minuten, " if arr[0] > 0
    ret += "#{arr[1]} Sekunden"
    ret
  end
end
