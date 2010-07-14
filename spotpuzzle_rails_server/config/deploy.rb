set :application, "spotpuzzle"
set :repository, "http://svn.elmermx.ch/"

set :scm, :subversion
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

role :web, "your web-server here"                          # Your HTTP server, Apache/etc
role :app, "your app-server here"                          # This may be the same as your `Web` server
role :db,  "your primary db-server here", :primary => true # This is where Rails migrations will run
role :db,  "your slave db-server here"

# If you are using Passenger mod_rails uncomment this:
# if you're still using the script/reapear helper you will need
# these http://github.com/rails/irs_process_scripts

# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end


set :user, 'fehrmir2'

set :domain, 'tipish.com'  # Dreamhost servername where your account is located
set :project, 'http://svn.tipish.com'  # Your application as its called in the repository
set :application, 'tipish.com'  # Your app's location (domain or sub-domain name as setup in panel)
set :applicationdir, "/home/#{user}/#{application}"  # The standard Dreamhost setup

# version control config
set :scm_username, 'deploy_production'
set :scm_password, 'indianer2010'
#set :repository, "http://svn.tipish.com/#{project}/trunk/"
set :repository, "http://svn.tipish.com/"
set :scm, :subversion

# roles (servers)
role :web, domain
role :app, domain
role :db,  domain, :primary => true
#role :db,  "tipish_prod", :primary => true # This is where Rails migrations will run

# deploy config
set :deploy_to, applicationdir
set :deploy_via, :export

# additional settings
default_run_options[:pty] = true  # Forgo errors when deploying from windows
#ssh_options[:keys] = %w(/Path/To/id_rsa)            # If you are using ssh_keys
set :chmod755, "app config db lib public vendor script script/* public/disp*"
set :use_sudo, false


# If you are using Passenger mod_rails uncomment this:
# if you're still using the script/reapear helper you will need
# these http://github.com/rails/irs_process_scripts
#
#namespace :db do
#  desc "[internal] Updates the symlink for database.yml file to the just deployed release."
#  task :symlink, :except => { :no_release => true } do
#    run "ln -nfs #{shared_path}/config/database.yml #{release_path}/config/database.yml"
#  end
#end
#
#namespace :public_upload do
#  desc "[internal] Updates the symlink for the upload dir to the just deployed release."
#  task :symlink, :except => { :no_release => true } do
#    run "ln -s #{shared_path}/public/system #{release_path}/public/system"
#  end
#end
#
#desc "[internal] Updates the symlink for the .htaccess file."
#task :htaccess_symlink, :except => { :no_release => true } do
#  run "ln -nfs #{shared_path}/public/.htaccess #{release_path}/public/.htaccess"
#end

desc "Link all the imporant files"
task :link_tipish_files, :except => { :no_release => true } do
  run "ln -nfs #{shared_path}/public/.htaccess #{release_path}/public/.htaccess"
  run "ln -nfs #{shared_path}/config/database.yml #{release_path}/config/database.yml"
  run "ln -nfs #{shared_path}/config/sphinx.yml #{release_path}/config/sphinx.yml"
  run "ln -nfs #{shared_path}/config/email.yml #{release_path}/config/email.yml"
end

desc "Generates sphinx config and indexes the sphinx index"
task :ts_config_and_index , :except => { :no_release => true } do
  ['thinking_sphinx:configure', 'thinking_sphinx:index', 'tipish:clean', 'tipish:deploy_tasks',
    'thinking_sphinx:stop', 'thinking_sphinx:start'].each do |act_rake_task|
    run("export RAILS_ENV=production; cd #{release_path} && /usr/bin/rake #{act_rake_task}")
  end

  ['thinking_sphinx:stop', 'tipish:remove_geotags', 'thinking_sphinx:start'].each do |act_rake_task|
    run("export RAILS_ENV=production; cd #{release_path} && /usr/bin/rake #{act_rake_task}") rescue ""
  end

end

namespace :deploy do
  task :start do ; end
  task :stop do ; end
  task :restart, :roles => :app, :except => { :no_release => true } do
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end

  after "deploy:finalize_update", "link_tipish_files", "ts_config_and_index"
  #, "public_upload:symlink"
end


#namespace :deploy do
#  desc "Start the application"
#  task :start, :roles => :app do
#    run "touch #{current_release}/tmp/restart.txt"
#    sudo "god start listeners"
#  end
#
#  desc "Stop the application"
#  task :stop, :roles => :app do
#    # Do nothing for application
#    sudo "god stop listeners"
#  end
#
#  desc "Restart Application"
#  task :restart, :roles => :app do
#    run "touch #{current_release}/tmp/restart.txt"
#    sudo "god restart listeners"
#  end
#end
