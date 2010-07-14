class PuzzleGame < ActiveRecord::Base
  named_scope :latest, :order => 'created_at DESC'
  named_scope :fastest, :limit => 3, :order => 'cycle_times ASC'
  named_scope :slowest, :limit => 3, :order => 'cycle_times DESC'
  named_scope :fewest_swaps, :limit => 3, :order => 'swap_times ASC'
  named_scope :most_swaps, :limit => 3, :order => 'swap_times DESC'
end
