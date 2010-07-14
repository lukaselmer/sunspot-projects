set :application, "puzzle.elmermx.ch"
set :repository, "https://svn.elmermx.ch/sunspot_projects/spotpuzzle_rails_server"

set :domain, 'puzzle.elmermx.ch'

set :scm, :subversion
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
#set :scm_username, 'elmer'

role :web, domain
role :app, domain
role :db,  domain, :primary => true

# If you are using Passenger mod_rails uncomment this:
# if you're still using the script/reapear helper you will need
# these http://github.com/rails/irs_process_scripts


set :project, "https://svn.elmermx.ch/sunspot_projects/spotpuzzle_rails_server"  # Your application as its called in the repository

set :user, 'root'
set :applicationdir, "/var/www/sunspotpuzzle"  # The standard Dreamhost setup

set :deploy_to, applicationdir
set :deploy_via, :export

# additional settings
default_run_options[:pty] = true  # Forgo errors when deploying from windows
#ssh_options[:keys] = %w(/Path/To/id_rsa)            # If you are using ssh_keys
set :chmod755, "app config db lib public vendor script script/* public/disp*"
set :use_sudo, false


desc "Link all the imporant files"
task :link_files, :except => { :no_release => true } do
  run "ln -nfs #{shared_path}/public/.htaccess #{release_path}/public/.htaccess"
  run "ln -nfs #{shared_path}/config/database.yml #{release_path}/config/database.yml"
end

desc "Chowns to user www-data"
task :chown_for_www_data, :except => { :no_release => true } do
  run "chown -R www-data:www-data #{shared_path}/ #{release_path}/"
end

namespace :deploy do
  task :start do ; end
  task :stop do ; end
  task :restart, :roles => :app, :except => { :no_release => true } do
    run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
  end
  after "deploy:finalize_update", "link_files", "chown_for_www_data"
end
