class CreateSpotActivities < ActiveRecord::Migration
  #address:string time_in_milliseconds:long celsius:double light:integer sw1:boolean sw2:boolean accel:double accelx:double accely:double accelz:double rel_accel:double rel_accelx:double rel_accely:double rel_accelz:double tiltx:double tilty:double tiltz:double
  def self.up
    create_table :spot_activities do |t|
      t.string :address
      t.long :time_in_milliseconds
      t.double :celsius
      t.integer :light
      t.boolean :sw1
      t.boolean :sw2
      t.double :accel
      t.double :accelx
      t.double :accely
      t.double :accelz
      t.double :rel_accel
      t.double :rel_accelx
      t.double :rel_accely
      t.double :rel_accelz
      t.double :tiltx
      t.double :tilty
      t.double :tiltz

      t.timestamps
    end
  end

  def self.down
    drop_table :spot_activities
  end
end
