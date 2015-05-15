package sparkle.cms.authoring.ui.web;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sparkle.cms.authoring.common.business.AssetManager;
import sparkle.cms.authoring.common.business.ContentManager;
import sparkle.cms.domain.AssetType;
import sparkle.cms.domain.CmsAsset;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

/**
 * UserController Created by thebaz on 12/04/15.
 */
@Controller(value = "assetController")
public class AssetController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RegistrationManager registrationManager;
	@Autowired
	private SiteManager siteManager;
	@Autowired
	private ContentManager contentManager;
	@Autowired
	private AssetManager assetManager;

    @ModelAttribute("allSites")
    public List<CmsSite> allSites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return siteManager.findAuthoredSites(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

	@ModelAttribute("allAssetTypes")
	public List<AssetType> assetTypes() {
		return Arrays.asList(AssetType.ALL);
	}
	
	@ModelAttribute("allContents")
	public List<CmsContent> allContents(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			CmsUser cmsUser = registrationManager.findUser(request
					.getRemoteUser());
			if (isWebmaster(cmsUser)) {
				return contentManager.findAllContents(cmsUser);
			} else if (isAuthor(cmsUser)) {
				return contentManager.findAuthoredContents(cmsUser);
			}
		} catch (RegistrationException e) {
			String msg = String.format("Cannot manage contents. Reason: %s",
					e.getMessage());
			logger.info(msg, e);
			response.sendError(400, msg);
		}
		return null;
	}

	@ModelAttribute("allAssets")
	public Page<CmsAsset> allAssets(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "pageSize", defaultValue = "12") int pageSize)
			throws IOException {
		try {
			CmsUser cmsUser = registrationManager.findUser(request
					.getRemoteUser());
			Pageable pageable = new PageRequest(page, pageSize,
					Sort.Direction.ASC, "name");
			if (isWebmaster(cmsUser)) {
				return assetManager.findAllAssets(cmsUser, pageable);
			} else if (isAuthor(cmsUser)) {
				return assetManager.findAuthoredAssets(cmsUser, pageable);
			}
		} catch (RegistrationException e) {
			String msg = String.format("Cannot manage assets. Reason: %s",
					e.getMessage());
			logger.info(msg, e);
			response.sendError(400, msg);
		}

		return null;
	}

	@RequestMapping({ "/assets" })
	public String show(ModelMap model) {
		model.put("assetData", new CmsAsset());
		model.put("mode", "add");
		return "assets";
	}
	
	/**
	 * POST /uploadFile -> receive and locally save a file.
	 * 
	 * @param uploadfile The uploaded file as Multipart file parameter in the 
	 * HTTP request. The RequestParam name must be the same of the attribute 
	 * "name" in the input tag with type file.
	 * 
	 * @return An http OK status in case of success, an http 4xx status in case 
	 * of errors.
	 */
	@RequestMapping(value = "/assets/upload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> uploadFile(
	    @RequestParam("uploadfile") MultipartFile uploadfile) {
		String filepath = "";
	  try {
	    // Get the filename and build the local file path (be sure that the 
	    // application have write permissions on such directory)
	    String filename = uploadfile.getOriginalFilename();
	    String directory = String.format("%s/uploaded_files", System.getProperty("java.io.tmpdir"));
	    filepath = Paths.get(directory, filename).toString();
	    
	    //TODO Local saving to be changed with cloud save mode 
	    BufferedOutputStream stream =
	        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	    stream.write(uploadfile.getBytes());
	    stream.close();
	  }
	  catch (Exception e) {
	    System.out.println(e.getMessage());
	    return new ResponseEntity<>(filepath, HttpStatus.BAD_REQUEST);
	  }
	  
	  return new ResponseEntity<>(filepath, HttpStatus.OK);
	}
}
